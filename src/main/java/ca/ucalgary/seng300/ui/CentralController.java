package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.client.ClientCore;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

// CentralController purpose is to hold the central container that each page's fxml will display in.
public class CentralController {
    private static CentralController instance;

    private GameGUIController gameUI;

    public TicTacToeController ttt;

    public void setTic(TicTacToeController t){
        ttt = t;
    }

    public ClientCore getCC(){
        return clientCore;
    }

    public ClientCore clientCore;
    public int test1;

    public void uptest(int i){
        test1 = i;
    }

    @FXML private StackPane viewContainer;

    @FXML
    public void initialize() {
        instance = this;
        clientCore = new ClientCore(this);
        clientCore.start();
        System.out.println("cc loaded");
        // REPLACE WITH LOGIN ONCE CREATED!
        loadView("GameGUI.fxml");
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
            gameUI = loader.getController();
            gameUI.setRoot(this);
            viewContainer.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
