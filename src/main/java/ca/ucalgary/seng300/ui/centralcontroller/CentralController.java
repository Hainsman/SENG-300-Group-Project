package ca.ucalgary.seng300.ui.centralcontroller;

import ca.ucalgary.seng300.MainApp;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.ui.login.SettingsController;
import ca.ucalgary.seng300.ui.windowmanager.WindowManager;
import ca.ucalgary.seng300.ui.dashboards.ProfileDashboardController;
import ca.ucalgary.seng300.ui.gamecontrollers.Connect4Controller;
import ca.ucalgary.seng300.ui.gamecontrollers.TicTacToeController;
import ca.ucalgary.seng300.ui.login.LoginController;
import ca.ucalgary.seng300.ui.login.NewUserController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.prefs.Preferences;

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
    public static int selectedGameId;
    public static int maxPlayers;
    public static String privacy;
    public static String lobbyCode;
    public static boolean lobbyPaired;
    public static boolean playWithAi;
    /** Display name from host lobby screen (optional). */
    public static String lobbyName;
    /** True if this client created the current lobby (may start the game). */
    public static boolean hostingLobby;
    public static String previousScreen;
    public static String player1Name;
    public static String player1Elo;
    public static String player1GamesPlayed;
    public static String player2Name;
    public static String player2Elo;
    public static String player2GamesPlayed;

    public TicTacToeController ttt;
    public Connect4Controller connect4;

    public void setTic(TicTacToeController t){
        ttt = t;
    }

    public void setConnect4(Connect4Controller c4) {
        connect4 = c4;
    }

    public ClientCore getCC(){
        return clientCore;
    }

    public ClientCore clientCore;
    private static final Preferences UI_PREFS = Preferences.userNodeForPackage(CentralController.class);
    private static final String PREF_TEXT_SIZE = "ui.text.size";
    private static final String PREF_COLORBLIND = "ui.color.blind";
    private static final String TEXT_SIZE_NORMAL = "normal";
    private static final String TEXT_SIZE_LARGE = "large";
    private static final String TEXT_SIZE_XLARGE = "xlarge";
    private static String currentTextSize = UI_PREFS.get(PREF_TEXT_SIZE, TEXT_SIZE_NORMAL);
    private static boolean currentColorBlind = UI_PREFS.getBoolean(PREF_COLORBLIND, false);

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
        titleLabel.setText(MainApp.TITLE);
        root.sceneProperty().addListener((obs, oldScene, newScene) -> applyUiPreferences());
        applyUiPreferences();

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
            if (ctrl instanceof LoginController lc) {
                lc.setCentralController(this);
            } else if (ctrl instanceof NewUserController nuc) {
                nuc.setCentralController(this);
            } else if (ctrl instanceof SettingsController sc) {
                sc.setCentralController(this);
            } else if (ctrl instanceof ProfileDashboardController pdc) {
                pdc.setCentralController(this);
            } else if (ctrl instanceof TicTacToeController ttc) {
                ttc.connectToRootForLobby(this);
            } else if (ctrl instanceof Connect4Controller c4) {
                c4.connectToRootForLobby(this);
            }
            viewContainer.setCenter(view);
            applyUiPreferences();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateAccessibilityPreferences(String textSize, boolean colorBlindMode) {
        currentTextSize = textSize == null ? TEXT_SIZE_NORMAL : textSize;
        currentColorBlind = colorBlindMode;
        UI_PREFS.put(PREF_TEXT_SIZE, currentTextSize);
        UI_PREFS.putBoolean(PREF_COLORBLIND, currentColorBlind);
        if (instance != null) {
            instance.applyUiPreferences();
        }
    }

    public static String getTextSizePreference() {
        return currentTextSize;
    }

    public static boolean isColorBlindModeEnabled() {
        return currentColorBlind;
    }

    private void applyUiPreferences() {
        if (root == null || root.getScene() == null) {
            return;
        }
        var stylesheets = root.getScene().getStylesheets();
        stylesheets.removeIf(s ->
                s.endsWith("accessibility_large.css")
                        || s.endsWith("accessibility_xlarge.css")
                        || s.endsWith("accessibility_colorblind.css"));

        if (TEXT_SIZE_LARGE.equals(currentTextSize)) {
            stylesheets.add(getClass().getResource("/ca.ucalgary.seng300.ui/stylesheets/accessibility_large.css").toExternalForm());
        } else if (TEXT_SIZE_XLARGE.equals(currentTextSize)) {
            stylesheets.add(getClass().getResource("/ca.ucalgary.seng300.ui/stylesheets/accessibility_xlarge.css").toExternalForm());
        }

        if (currentColorBlind) {
            stylesheets.add(getClass().getResource("/ca.ucalgary.seng300.ui/stylesheets/accessibility_colorblind.css").toExternalForm());
        }
    }
}
