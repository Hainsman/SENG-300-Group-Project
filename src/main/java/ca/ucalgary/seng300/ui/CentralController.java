package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.client.ClientCoreV2;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

// CentralController purpose is to hold the central container that each page's fxml will display in.
public class CentralController {

    private static CentralController instance;

    private WindowManager windowManager;

    @FXML private StackPane root;
    @FXML private BorderPane viewContainer;
    @FXML private BorderPane titleBarPane;
    @FXML private Label titleLabel;
    @FXML private Button quitButton;
    @FXML private Button minimizeButton;
    @FXML private Button maximizeButton;

    public static String selectedGame;
    public static int maxPlayers;
    public static String privacy;
    public static String lobbyCode;
    /** Display name from host lobby screen (optional). */
    public static String lobbyName;
    /** True if this client created the current lobby (may start the game). */
    public static boolean hostingLobby;
    public static String previousScreen;
    /** Need a way to get the specific instances of each subscreen for clientcore **/
    private GameGUIController gameUI;
    public ClientCore clientCore = null;
    public LoginController loginController = null;
    public NewUserController newUserController = null;
    public Connect4Controller C4;
    public TicTacToeController ttt;

    public ClientCore getCC(){
        return clientCore;
    }


    @FXML
    public void initialize() {
        instance = this;
        clientCore = new ClientCore(this);
        clientCore.start();
        System.out.println("cc loaded");

        WindowManager.WindowControls controls = new WindowManager.WindowControls(
                root,
                titleBarPane,
                titleLabel,
                quitButton,
                minimizeButton,
                maximizeButton
        );

        windowManager = new WindowManager(controls);

        loadView("Login.fxml");
    }

    public static CentralController getInstance() {
        return instance;
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
            Object ctrl = loader.getController();
            switch(ctrl){
                case GameGUIController gu:
                    gameUI = gu;
                    gameUI.setRoot(this);
                    break;
                case LoginController lc:
                    loginController = lc;
                    loginController.setRoot(this);
                    break;
                case NewUserController nu:
                    newUserController = nu;
                    newUserController.setRoot(this);
                    break;
                case Connect4Controller cccc:
                    C4 = cccc;
                    C4.setRoot(this);
                    break;
                case TicTacToeController tic:
                    ttt = tic;
                    ttt.setRoot(this);
                    break;
                default:
                    System.out.println("Scene "+ ctrl.getClass()+ "doesn't have client core access");
                    break;
            }/*
            if (ctrl instanceof GameGUIController gfc) {
                gameUI = gfc;
                gameUI.setRoot(this);
            } else if (ctrl instanceof TicTacToeController ttc) {
                ttc.connectToRootForLobby(this);
            }*/
            viewContainer.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
