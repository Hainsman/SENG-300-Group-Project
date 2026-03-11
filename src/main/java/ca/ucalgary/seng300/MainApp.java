package ca.ucalgary.seng300;

import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.Store;
import ca.ucalgary.seng300.platform.utils.Helper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("JavaFX 25 + JDK 25 OK");
        Scene scene = new Scene(label, 1020, 680);
        stage.setScene(scene);
        stage.setTitle("Starter");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
