package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.game.core.GameType;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;

import java.util.ArrayList;

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

    private final ArrayList<Navigable> nodes = new ArrayList<>();

    public static GameType selectedGame;
    public static int maxPlayers;
    public static String privacy;
    public static String lobbyCode;
    /** Display name from host lobby screen (optional). */
    public static String lobbyName;
    /** True if this client created the current lobby (may start the game). */
    public static boolean hostingLobby;
    public static Navigation previousScreen;
    public static Navigation currentScreen;

//    /** FXML resource name for the profile / stats dashboard center view. */
//    public static final String PROFILE_DASHBOARD_FXML = "ProfileDashboard.fxml";
//
//    /**
//     * Tic-Tac-Toe play surface: sidebar + embedded {@code TicTacToe.fxml}, wired to {@link ClientCore} for online.
//     */
//    public static final String TIC_TAC_TOE_SHELL_FXML = "GameGUI.fxml";

    /**
     * When true, {@link GameGUIController} wires Tic-Tac-Toe to a local session with a random-move AI
     * opponent (player is X, AI is O). Cleared when leaving that flow or the game screen.
     */
    public static boolean ticTacToeLocalAiMode = false;

    /**
     * When true, {@link C4} uses a local {@link ca.ucalgary.seng300.platform.session.ConnectFourSession}
     * with a random-move AI as player 2 (same pattern as TTT vs AI).
     */
    public static boolean connect4LocalAiMode = false;

    public static int currentUserId;
    public static String displayName = "Player";

    public static String rosterHostName = "";
    public static int rosterHostElo = 1200;
    public static String rosterGuestName = "";
    public static int rosterGuestElo = 1200;

    //private GameGUIController gameUI;

    public static void clearLobbyRoster() {
        rosterHostName = "";
        rosterGuestName = "";
        rosterHostElo = 1200;
        rosterGuestElo = 1200;
    }

    /**
     * Clears signed-in session and lobby navigation state, then shows the login screen.
     */
    public static void logout() {
        currentUserId = 0;
        displayName = "Player";
        previousScreen = null;
        selectedGame = null;
        ticTacToeLocalAiMode = false;
        connect4LocalAiMode = false;
        hostingLobby = false;
        lobbyCode = null;
        lobbyName = null;
        maxPlayers = 0;
        privacy = null;
        clearLobbyRoster();
        if (instance != null) {
//            moveTo("Login.fxml");
            moveTo(Navigation.LOGIN);
        }
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

    public TTT ttt;
    public C4 connect4;

    public void setTic(TTT t){
        ttt = t;
    }

    public void setConnect4(C4 c4) {
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

        initializeNodes();

        // Start at auth, then GameDashboardNew → chooseModeNew → lobby or GameGUI (incl. Play with AI).
//        loadView("Login.fxml");
        loadView(Navigation.LOGIN);
    }

    private void initializeNodes() {
        GameGUI gameGUI = new GameGUI();
        nodes.add(gameGUI);
        nodes.add(new C4(this, gameGUI));
        nodes.add(new TTT(this, gameGUI));
        nodes.add(new ChooseMode());
        nodes.add(new GameDashboard());
        nodes.add(new HostLobby());
        nodes.add(new Info());
        nodes.add(new JoinLobby());
        nodes.add(new Login());
        nodes.add(new MainLobby());
        nodes.add(new NewUser());
        nodes.add(new ProfileDashboard());
        nodes.add(new Settings());
    }

    public static CentralController getInstance() {
        return instance;
    }

    // call anytime to switch pages
    public static void moveTo(Navigation nav) {
        previousScreen = currentScreen;
        instance.loadView(nav);
    }

    /**
     * Re-applies text scaling, window size, and colorblind root class for the whole app.
     */
    public static void applyUiPreferences() {
        if (instance != null) {
            instance.syncUiPreferencesToScene();
        }
    }

    private Navigable getNode(Navigation nav) {
        for (Navigable node : nodes)
            if (node.getNavigation() == nav)
                return node;
        return null;
    }

    // private method used by navigateTo
    private void loadView(Navigation nav) {
        Navigable node = getNode(nav);

        if (nav == Navigation.TTT || nav == Navigation.C4) {
            GameGUI gameGUI = (GameGUI) getNode(Navigation.GAME_GUI);
            viewContainer.setCenter(gameGUI);
            gameGUI.setGameNode((Node) node);
            gameGUI.reset();
        }
        else {
            viewContainer.setCenter((Node) node);
        }

        node.reset();

        currentScreen = nav;
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/ca.ucalgary.seng300.ui/" + fxml)
//            );
//            Node view = loader.load();
//            Object ctrl = loader.getController();
//            if (ctrl instanceof GameGUIController gfc) {
//                gameUI = gfc;
//                gameUI.setRoot(this);
//            } else if (ctrl instanceof TTT ttc) {
//                ttc.connectToRootForLobby(this);
//            } else if (ctrl instanceof C4 c4) {
//                c4.connectToRootForLobby(this);
//            }
//            viewContainer.setCenter(view);
//            syncUiPreferencesToScene();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void syncUiPreferencesToScene() {
        Parent sceneRoot = root;
        Region region = root instanceof Region r ? r : null;
        double factor = AppUiPreferences.textScaleFactor;

        sceneRoot.getStyleClass().remove("colorblind-mode");
        if (AppUiPreferences.colorblindMode) {
            sceneRoot.getStyleClass().add("colorblind-mode");
        }
        String path = "/ca.ucalgary.seng300.ui/stylesheets/";
        sceneRoot.getStylesheets().removeIf(sheet ->
                sheet.contains(path + "smallText.css") ||
                        sheet.contains(path + "largeText.css") ||
                        sheet.contains(path + "extraLargeText.css")
        );

        if(factor == 0.9){
            sceneRoot.getStylesheets().add(path + "smallText.css");
        }else if(factor == 1.1){
            sceneRoot.getStylesheets().add(path + "largeText.css");
        }else if(factor == 1.25){
            sceneRoot.getStylesheets().add(path +"extraLargeText.css");
        }else {
            sceneRoot.getStylesheets().removeIf(sheet ->
                    sheet.contains(path + "smallText.css") ||
                            sheet.contains(path + "largeText.css") ||
                            sheet.contains(path + "extraLargeText.css")
            );
        }
    }
}
