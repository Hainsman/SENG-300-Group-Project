package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.client.ClientCore;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;

// CentralController purpose is to hold the central container that each page's fxml will display in.
public class CentralController {

    private static CentralController instance;

    private WindowManager windowManager;

    private Scale uiScaleTransform;
    private ChangeListener<Number> uiScalePivotListener;

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

    /** FXML resource name for the profile / stats dashboard center view. */
    public static final String PROFILE_DASHBOARD_FXML = "ProfileDashboard.fxml";

    public static int currentUserId;
    public static String displayName = "Player";

    public static String rosterHostName = "";
    public static int rosterHostElo = 1200;
    public static String rosterGuestName = "";
    public static int rosterGuestElo = 1200;

    private GameGUIController gameUI;

    public static void clearLobbyRoster() {
        rosterHostName = "";
        rosterGuestName = "";
        rosterHostElo = 1200;
        rosterGuestElo = 1200;
    }

    public static void applyLobbyRosterPayload(String payload) {
        if (payload == null || payload.isBlank()) {
            return;
        }
        String[] p = payload.split("\\|", 4);
        if (p.length < 4) {
            return;
        }
        rosterHostName = p[0].trim();
        rosterHostElo = parseEloSegment(p[1]);
        rosterGuestName = p[2].trim();
        rosterGuestElo = parseEloSegment(p[3]);
    }

    private static int parseEloSegment(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 1200;
        }
    }

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
    public int test1;

    public void uptest(int i){
        test1 = i;
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

        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                syncUiPreferencesToScene();
            }
        });

        loadView("Login.fxml");
    }

    public static CentralController getInstance() {
        return instance;
    }

    // call anytime to switch pages
    public static void moveTo(String fxml) {
        instance.loadView(fxml);
    }

    /**
     * Re-applies text scaling, window size, and colorblind root class for the whole app.
     */
    public static void applyUiPreferences() {
        if (instance != null) {
            instance.syncUiPreferencesToScene();
        }
    }

    // private method used by navigateTo
    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ca.ucalgary.seng300.ui/" + fxml)
            );
            Node view = loader.load();
            Object ctrl = loader.getController();
            if (ctrl instanceof GameGUIController gfc) {
                gameUI = gfc;
                gameUI.setRoot(this);
            } else if (ctrl instanceof TicTacToeController ttc) {
                ttc.connectToRootForLobby(this);
            } else if (ctrl instanceof Connect4Controller c4) {
                c4.connectToRootForLobby(this);
            }
            viewContainer.setCenter(view);
            syncUiPreferencesToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void syncUiPreferencesToScene() {
        Parent sceneRoot = root;
        Region region = root instanceof Region r ? r : null;
        double factor = AppUiPreferences.textScaleFactor;

        if (uiScaleTransform != null) {
            sceneRoot.getTransforms().remove(uiScaleTransform);
            if (uiScalePivotListener != null && region != null) {
                region.widthProperty().removeListener(uiScalePivotListener);
                region.heightProperty().removeListener(uiScalePivotListener);
            }
            uiScaleTransform = null;
            uiScalePivotListener = null;
        }

        sceneRoot.getStyleClass().remove("colorblind-mode");
        if (AppUiPreferences.colorblindMode) {
            sceneRoot.getStyleClass().add("colorblind-mode");
        }

        if (factor != 1.0 && region != null) {
            uiScaleTransform = new Scale(factor, factor);
            uiScalePivotListener = (obs, oldV, newV) ->
                    updateScalePivot(region, uiScaleTransform);
            region.widthProperty().addListener(uiScalePivotListener);
            region.heightProperty().addListener(uiScalePivotListener);
            updateScalePivot(region, uiScaleTransform);
            sceneRoot.getTransforms().add(uiScaleTransform);
        }

        Scene scene = sceneRoot.getScene();
        if (scene != null && scene.getWindow() instanceof Stage stage) {
            double w = WindowManager.WINDOW_MINIMUM_WIDTH * factor;
            double h = WindowManager.WINDOW_MINIMUM_HEIGHT * factor;
            stage.setMinWidth(w);
            stage.setMinHeight(h);
            if (!stage.isMaximized()) {
                stage.setWidth(w);
                stage.setHeight(h);
            }
        }
    }

    private static void updateScalePivot(Region layoutRoot, Scale scale) {
        if (scale == null || layoutRoot == null) {
            return;
        }
        double w = layoutRoot.getLayoutBounds().getWidth();
        double h = layoutRoot.getLayoutBounds().getHeight();
        if (w <= 0 || h <= 0) {
            w = layoutRoot.prefWidth(-1);
            h = layoutRoot.prefHeight(-1);
        }
        if (w > 0 && h > 0) {
            scale.setPivotX(w / 2.0);
            scale.setPivotY(h / 2.0);
        }
    }
}
