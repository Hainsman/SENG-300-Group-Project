package ca.ucalgary.seng300.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

// CentralController purpose is to hold the central container that each page's fxml will display in.
public class CentralController {
    private static CentralController instance;

    @FXML private StackPane viewContainer;

    public static String selectedGame;

    @FXML
    public void initialize() {
        instance = this;
        // REPLACE WITH LOGIN ONCE CREATED!
        loadView("GameDashboard.fxml");
    }

    // call anytime to switch pages
    public static void moveTo(String fxml) {
        instance.loadView(fxml);
    }

    // private method used by navigateTo
    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ca.ucalgary.seng300.ui/" + fxml)
            );
            Node view = loader.load();
            viewContainer.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
