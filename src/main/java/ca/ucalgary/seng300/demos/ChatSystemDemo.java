package ca.ucalgary.seng300.demos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatSystemDemo extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatSystemDemo.class.getResource("/ca/ucalgary/seng300/chatDemo.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setScene(scene);
        stage.setTitle("Starter");
        stage.show();
    }

    static void main(String[] args) {
        launch(args);
    }
}
