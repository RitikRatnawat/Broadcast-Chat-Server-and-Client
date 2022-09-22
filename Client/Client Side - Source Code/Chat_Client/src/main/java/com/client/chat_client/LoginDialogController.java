package com.client.chat_client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LoginDialogController
{
    private String username;

    @FXML
    private GridPane loginDialog;
    @FXML
    private TextField name;

    @FXML
    public void onLoginButtonPressed()
    {
        if(name.getText().trim().length() == 0)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Invalid Username");
            alert.setContentText("Please enter valid username");
            alert.showAndWait();
            name.setText("");
            return;
        }

        username = name.getText().trim();
        loginDialog.getScene().getWindow().hide();
    }

    public String getUsername()
    {
        return username;
    }
}
