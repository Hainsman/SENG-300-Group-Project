package ca.ucalgary.seng300;

import ca.ucalgary.seng300.ui.WindowManager;
import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.utils.Helper;
import ca.ucalgary.seng300.ui.CentralController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class MainApp extends Application {

    public static final String TITLE = "BOGO Solutions Game Application";
    public static final String VERSION = "1.0";

    @Override
    public void start(Stage stage) throws Exception {
        // load Root.fxml, an empty container to be used as base for moving through UI.
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ca.ucalgary.seng300.ui/root.fxml")
        );

        // This is for custom window appearance
        stage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(
                loader.load(),
                WindowManager.WINDOW_MINIMUM_WIDTH,
                WindowManager.WINDOW_MINIMUM_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
