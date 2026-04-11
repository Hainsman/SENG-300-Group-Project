package ca.ucalgary.seng300;

import ca.ucalgary.seng300.ui.windowmanager.WindowManager;
import ca.ucalgary.seng300.platform.store.SessionHistoryStore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class MainApp extends Application {

    public static final String TITLE = "BOGO solutions Game Application V2.0";
    public static final String VERSION = "2.0";

    @Override
    public void start(Stage stage) throws Exception {
        // load Root.fxml, an empty container to be used as base for moving through UI.
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ca.ucalgary.seng300.ui/Root.fxml")
        );

        // This is for custom window appearance
        stage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(
                loader.load(),
                WindowManager.WINDOW_MINIMUM_WIDTH,
                WindowManager.WINDOW_MINIMUM_HEIGHT);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> SessionHistoryStore.getInstance().clearSessions());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
