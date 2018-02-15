/*
    Class to connect to database
*/
package com.example.frederic.genericapp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Frederic on 2/15/2018.
 */

/*
    Class to handle connections to the database PostgreSQL, should be used in a thread
    TODO: AWAIT RESTful WEB SERVICE IMPLEMENTATION
*/
public class DatabaseConnector {
    private static final String SERVERIP = "8.8.8.8";
    private static final int PORTNO = 5432;

    public boolean checkConnection(){
        // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress(SERVERIP,PORTNO);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }

    }

}
