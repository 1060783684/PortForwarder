package server;

import java.io.IOException;

/**
 * Created by root on 18-1-18.
 */
public class Server {
    public static void main(String[] args){
        PortForwarderServer server = new PortForwarderServer();
        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
