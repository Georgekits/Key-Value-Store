package gr.hua.dit.Store;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server  {
    
    //Creates Master Node Hashmap
    static HashMap<String, String> MasterNode = new HashMap<String, String>();

    //Creates 5 nodes using node class
    //Each node contains a hashmap and a thread
    static node node1 = new node();
    static node node2 = new node();
    static node node3 = new node();
    static node node4 = new node();
    static node node5 = new node();
    
    //Global variables for mapping pair to nodes
    static int Counter = 0;
    static int Coordinator;

    public static void main(String[] args) throws Exception {
        //Creates a HTTP server listening to Socket address 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        //Utillities
        server.createContext("/store", new WelcomeInfo());
        server.createContext("/put", new PutHandler());
        server.createContext("/get", new GetHandler());

        server.setExecutor(null); // creates a default executor
        server.start();  //Start server

        System.out.println("The server is running");
        
    }

    // All starts from here : http://localhost:8000/store
    static class WelcomeInfo implements HttpHandler {//Throw at client-side a some kind of menu

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "Welcome to our Distributed Key-Value-Store" + "\n\n"
                    + "You can put pairs of [key,value] and" + "\n"
                    + "get values from a specific key." + "\n\n"
                    + "Put command: Use /put?key=YOUR_KEY&value=YOUR_VALUE" + "\n"
                    + "Get command: Use /get?key=YOUR_KEY";
            Server.writeResponse(httpExchange, response);
        }
    }

    //Put the pair of key and value to the hashmap!
    static class PutHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            //Creates an empty string Builder
            StringBuilder response = new StringBuilder();
            
            //Gets key and value from URL
            Map<String, String> parms = Server.queryToMap(httpExchange.getRequestURI().getQuery());
            String key = parms.get("key");
            String value = parms.get("value");

            //Choose where to put key and value using modulo  
            Coordinator = Counter % 5;
            Coordinator++;
            Counter++;
            
            //Checks if key is already been registered
            //in Master Node
            //If Master Node contains key then go to the node that contains the key
            //and remove in order to achieve consistency
            if(MasterNode.containsKey(key)) {
                String node = MasterNode.get(key);
                if(node.equals("node1")) node1.getMap().remove(key);
                if(node.equals("node2")) node2.getMap().remove(key);
                if(node.equals("node3")) node3.getMap().remove(key);
                if(node.equals("node4")) node4.getMap().remove(key);
                if(node.equals("node5")) node5.getMap().remove(key);
                MasterNode.remove(key);
                MasterNode.put(key, "node" + Coordinator + ""); 
            } else {
                MasterNode.put(key, "node" + Coordinator + ""); 
            }
            
            //Put key and value pair to nodes according to Coordinator
            if (Coordinator == 1) {
                node1.setKey(key);
                node1.setValue(value);
                node1.run();
            }
            if (Coordinator == 2) {
                node2.setKey(key);
                node2.setValue(value);
                node2.run();
            }
            if (Coordinator == 3) {
                node3.setKey(key);
                node3.setValue(value);
                node3.run();
            }
            if (Coordinator == 4) {
                node4.setKey(key);
                node4.setValue(value);
                node4.run();
            }
            if (Coordinator == 5) {
                node5.setKey(key);
                node5.setValue(value);
                node5.run();
            }
            
            //Send success response to client
            response.append("Your pair [").append(key).append(",").append(value).append("] has been succesfully registered");
            Server.writeResponse(httpExchange, response.toString());
        }
    }

    //Gets the value of the key that the client sends
    static class GetHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            //Creates an empty string Builder
            StringBuilder response = new StringBuilder();
            
            //Gets key from URL
            Map<String, String> parms = Server.queryToMap(httpExchange.getRequestURI().getQuery());          
            String key = parms.get("key");
            
            //Checks if the key exists in the hashmap,
            //if not sends client error message.
            if (!MasterNode.containsKey(key)) {
                response.append("This key does not exist");
            } else {   
                //Get value(which Node contains key) from Master Node using key
                String node = MasterNode.get(key);
                String value = null;
                //Go to node where key exists and get value
                if(node.equals("node1")) value = node1.getMap().get(key);
                if(node.equals("node2")) value = node2.getMap().get(key);    
                if(node.equals("node3")) value = node3.getMap().get(key);
                if(node.equals("node4")) value = node4.getMap().get(key);
                if(node.equals("node5")) value = node5.getMap().get(key);
                //Send value to client
                response.append("Value at index {" + key + "} is: " + value);
            }
            Server.writeResponse(httpExchange, response.toString());
        }
    }

    //Sends response to client
    public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    //Gets URL query and parse key and value
    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }
}
