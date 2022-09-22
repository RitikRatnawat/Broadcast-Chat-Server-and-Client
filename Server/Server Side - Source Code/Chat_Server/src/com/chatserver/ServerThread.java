package com.chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

// Server threads to handle multiple clients
public class ServerThread extends Thread
{
    private Socket socket;
    private ArrayList<Socket> clients;
    private ArrayList<String> users;
    private String userName;

    // Server Thread Constructor
    public ServerThread(Socket skt, ArrayList<Socket> clts, ArrayList<String> usrs)
    {
        try
        {
            // Initializing Thread member variables
            this.socket = skt;
            this.clients = clts;
            this.users = usrs;

            // Getting Username
            DataInputStream input = new DataInputStream(skt.getInputStream());
            userName = input.readUTF();

            System.out.println(userName + " logged in at " + new Date());

            // Adding socket to active clients
            clients.add(skt);
            users.add(userName);
        }
        catch (Exception e)
        {
            System.err.println("Client Error");
        }

        // Broadcast to every client about new User login
        tellEveryOne("****** " + userName + " Logged in at " + (new Date()) + " ******");
        sendNewUsersList();

    }

    @Override
    public void run()
    {
        String message;     // To get message from client

        try
        {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            do
            {
                message = input.readUTF();      // Reading message from Datastream

                if(message.equals(Server.LOGOUT_MESSAGE))   // Logout mechanism
                {
                    System.out.println(userName+" Logged out at "+(new Date()));
                    break;
                }

                // Broadcast to every client about user Message
                tellEveryOne(userName + " said : " + message);
            }
            while(true);

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF(Server.LOGOUT_MESSAGE);
            output.flush();

            // Broadcast to every client about user Logout
            tellEveryOne("****** "+userName+" Logged out at "+(new Date())+" ******");

            // Remove client from active clients
            users.remove(userName);
            clients.remove(socket);

            // Update users list on each client
            sendNewUsersList();

            // Close the socket
            socket.close();
        }
        catch (Exception e)
        {
            System.err.println("Client Error (At " + userName + ") : " + e.getMessage());
        }
    }

    // function to send new user list to each active clients
    public void sendNewUsersList()
    {
        tellEveryOne(Server.UPDATE_USERS + users.toString());
    }

    // Function to broadcast information to each active client
    public void tellEveryOne(String message)
    {
        try
        {
            for (Socket s : clients)
            {
                DataOutputStream output = new DataOutputStream(s.getOutputStream());
                output.writeUTF(message);
                output.flush();
            }
        }
        catch(Exception e)
        {
            System.err.println("Client Error (At " + userName + ")" + e.getMessage());
        }
    }
}
