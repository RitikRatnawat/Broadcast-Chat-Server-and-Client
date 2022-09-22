module com.example.chat_client {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.client.chat_client to javafx.fxml;
    exports com.client.chat_client;
}