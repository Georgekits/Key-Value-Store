package gr.hua.dit.Store;

import java.util.HashMap;

public class node implements Runnable {
    
    //Fields
    private HashMap<String, String> map = new HashMap<String, String>();
    private String key;
    private String value;

    //Empty constructor
    public node() {

    }
    
    //Setters and Getters
    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    //Run 
    @Override
    public void run() {
        //Puts key and value pair to hashmap
        map.put(key, value);
    }
}
