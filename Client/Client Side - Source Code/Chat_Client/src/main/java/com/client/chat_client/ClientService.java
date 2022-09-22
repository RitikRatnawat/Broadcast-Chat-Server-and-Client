package com.client.chat_client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import java.io.DataInputStream;
import java.util.StringTokenizer;

public class ClientService extends ScheduledService<ObservableList<String>>
{
    private DataInputStream input;

    public ClientService(DataInputStream input)
    {
        this.input = input;
    }

    @Override
    protected Task<ObservableList<String>> createTask()
    {
        return new Task<ObservableList<String>>()
        {
            @Override
            protected ObservableList<String> call() throws Exception
            {
                String message;

                message = input.readUTF();
                ObservableList<String> activeUsers = FXCollections.emptyObservableList();

                if (message.startsWith("Update Users List"))
                {
                    activeUsers = updateUsersList(message);
                    updateValue(activeUsers);
                }
                else if (message.equals("@@ LogOut Me @@"))
                        return null;
                else
                    updateMessage(message + "\n");

                return activeUsers;
            }
        };
    }

    public ObservableList<String> updateUsersList(String message)
    {
        ObservableList<String> activeUsers = FXCollections.observableArrayList();

        try
        {
            message = message.replace("[", "");
            message = message.replace("]", "");
            message = message.replace("Update Users List", "");

            StringTokenizer st = new StringTokenizer(message, ",");

            while (st.hasMoreTokens())
            {
                String temp = st.nextToken();
                activeUsers.add(temp.trim());
            }
        }
        catch(Exception e)
        {
            System.err.println("Updating Users Error : "+e.getMessage());
        }

        return activeUsers;
    }
}
