package ca.ucalgary.seng300;

import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.UserStoreFile;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("JavaFX 25 + JDK 25 OK");
        Scene scene = new Scene(label, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Starter");
        stage.show();
        UserStoreFile usf = new UserStoreFile();
        usf.create(new User(1, "Daniel", "123"));
        usf.create(new User(2, "Alex", "456"));
        usf.create(new User(3, "John", "789"));
        User Paul = new User(4, "Paul", "101112");
        usf.create(Paul);
        usf.delete(Paul);
        for (var id: usf.userMap.keySet()){
            System.out.println(id);
            User user = usf.userMap.get(id);
            System.out.println("ID: " + id.toString() + ", User: " + user.getlogin() + ", Password: " + user.getPasswordHash());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
