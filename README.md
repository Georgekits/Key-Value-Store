# Key-Value-Store
Client -  Server TCP/IP Communication

ABOUT:


This project is an implemented distributed key-value store using Java Standard Edition.
The system will allow key-value pairs (put) to be entered from simple Strings to 5 nodes but also key-value retrieval (get).
The architecture is directory-based, so server is a process that decides how to allocate requests from clients to 5 threads (nodes).
The data will be stored in memory, so each of the threads will use its own HashMap object.
Moreover, it returns the correct consistency or an error message if the value does not exist.
