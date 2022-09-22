package com.chatserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

// Multithreaded Server
public class Server
{
    private ArrayList<Socket> clients = new ArrayList<>();  // For storing active clients
    private ArrayList<String> users = new ArrayList<>();   // For storing usernames of active clients

    private ServerSocket serverSocket;    // Server Socket for accepting requests
    private Socket socket;   // Socket for keeping track which client requesting

    public static final int PORT = 10;  // Port on which Server is running
    public static final String UPDATE_USERS = "Update Users List";
    public static final String LOGOUT_MESSAGE = "@@ LogOut Me @@";

    // Server Constructor
    public Server()
    {
        try
        {
            // Initialising Server Socket
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server Started : " + new Date());

            while(true)
            {
                // Handling clients on different threads
                socket = serverSocket.accept();
                Thread st = new ServerThread(socket, clients, users);
                st.start();
            }
        }
        catch (Exception e)
        {
            System.err.println("Server Error (At Starting) : " + e.getMessage());
        }
    }

    public static void main(String[] args)
    {
        // Starting Server
        new Server();
    }
}
