package ca.ucalgary.seng300;

import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.utils.Helper;
import ca.ucalgary.seng300.ui.CentralController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class MainApp extends Application {

    public static final String TITLE = "I Swear I am not Gay.";
    public static final String VERSION = "1.0";

    @Override
    public void start(Stage stage) throws Exception {
        // load Root.fxml, an empty container to be used as base for moving through UI.
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ca.ucalgary.seng300.ui/Root.fxml")
        );
        Scene scene = new Scene(loader.load(), 1020, 680);
        stage.setScene(scene);
        stage.setTitle("Bogo Solutions");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
