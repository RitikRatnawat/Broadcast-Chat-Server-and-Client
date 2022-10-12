package com.client.chat_client;

import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientController
{
    private String username;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    @FXML
    private BorderPane main_window;

    @FXML
    private Label header;

    @FXML
    private TextArea messageField, messageWindow;

    @FXML
    private Button loginButton, logoutButton;

    @FXML
    private ListView<String> usersList;

    private ClientService service;


    @FXML
    public void initialize()
    {
        logoutButton.setDisable(true);
        usersList.setMouseTransparent(true);
        usersList.setFocusTraversable(false);
    }

    public void onClickMessageArea()
    {
        if(messageField.getText().equals("Type your message here..."))
        {
            messageField.setEditable(true);
            messageField.setText("");
        }
    }

    public void onReleasedMessageArea()
    {
        if(messageField.getText().trim().length() == 0)
        {
            messageField.setText("Type your message here...");
            messageField.setEditable(false);
        }
    }

    @FXML
    public void onLogin()
    {
        try
        {
            Stage loginStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("LoginDialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 380, 120);
            loginStage.initOwner(main_window.getScene().getWindow());
            loginStage.setTitle("Login");
            loginStage.setResizable(false);
            loginStage.setScene(scene);
            main_window.disableProperty().bind(loginStage.showingProperty());
            loginStage.showAndWait();

            LoginDialogController controller = fxmlLoader.getController();
            username = controller.getUsername();

            if(username != null)
            {
                socket = new Socket(InetAddress.getLocalHost(), 10);
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());

                output.writeUTF(username);
                header.setText(username + "\'s Chat Client");
                loginButton.setDisable(true);
                logoutButton.setDisable(false);

                service = new ClientService(input);
                service.setPeriod(Duration.seconds(2));

                service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent)
                    {
                        messageWindow.appendText(workerStateEvent.getSource().getMessage());

                        ObservableList<String> users = (ObservableList<String>) workerStateEvent.getSource().getValue();
                        usersList.setItems(users);
                    }
                });

                service.start();
            }
        }
        catch(Exception e)
        {
            System.err.println("Login Error : " + e.getMessage());
        }
    }

    public void onSend()
    {
        String message;

        if(socket == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Not Logged In");
            alert.setHeaderText("You are not logged in right now.");
            alert.showAndWait();
            return;
        }

        try
        {
            message = messageField.getText();

            if(message.trim().length() == 0 ||
                    message.equals("Type your message here..."))
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Please Enter Something");
                alert.setContentText("Please enter the message to Send");
                alert.showAndWait();
                return;
            }

            output.writeUTF(message);
            messageField.setText("Type your message here...");
        }
        catch (Exception e)
        {
            System.err.println("Sending Error : " + e.getMessage());
        }

    }

    @FXML
    public void onLogout()
    {
        if(socket == null)
            return;

        try
        {
            output.writeUTF("@@ LogOut Me @@");
            header.setText("Broadcast Chat Client");
            Thread.sleep(200);
            socket = null;
            service.cancel();
        }
        catch(Exception e)
        {
            System.err.println("Logout Error : "+e.getMessage());
        }
        
        loginButton.setDisable(false);
        logoutButton.setDisable(true);
    }

    @FXML
    public void onExit()
    {
        try
        {
            if(output != null)
                output.writeUTF("@@ LogOut Me @@");

            if(service != null)
                service.cancel();

            Thread.sleep(200);
            socket = null;
        }
        catch(Exception e)
        {
            System.err.println("Exit Error : "+e.getMessage());
        }

        main_window.getScene().getWindow().hide();
    }
}