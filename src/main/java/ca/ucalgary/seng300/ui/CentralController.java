package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.ui.games.C4Page;
import ca.ucalgary.seng300.ui.games.GameGUIPage;
import ca.ucalgary.seng300.ui.games.TTTPage;
import ca.ucalgary.seng300.ui.lobby.*;
import ca.ucalgary.seng300.ui.userdata.*;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
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
import javafx.stage.Window;

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

    public TTTPage tttPage;
    public C4Page connect4;
    private GameGUIPage gameGuiPage;

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

    /**
     * When true, {@link GameGUIController} wires Tic-Tac-Toe to a local session with a random-move AI
     * opponent (player is X, AI is O). Cleared when leaving that flow or the game screen.
     */
    public static boolean ticTacToeLocalAiMode = false;

    /**
     * When true, {@link C4Page} uses a local {@link ca.ucalgary.seng300.platform.session.ConnectFourSession}
     * with a random-move AI as player 2 (same pattern as TTT vs AI).
     */
    public static boolean connect4LocalAiMode = false;

    public static int currentUserId;
    public static String displayName = "Player";

    public static String rosterHostName = "";
    public static int rosterHostElo = 1200;
    public static String rosterGuestName = "";
    public static int rosterGuestElo = 1200;

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

    public ClientCore getCC(){
        return clientCore;
    }

    public void addIncomingMatchChat(String sender, String message) {
        if (gameGuiPage == null || message == null || message.isBlank()) {
            return;
        }
        gameGuiPage.addChat(sender != null && !sender.isBlank() ? sender : "Player", message, javafx.scene.paint.Color.web("#86efac"));
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
        clientCore.start();
        System.out.println("cc loaded");

        // Start at auth, then GameDashboardNew → chooseModeNew → lobby or GameGUI (incl. Play with AI).
        loadView(Navigation.LOGIN);
    }

    private void initializeNodes() {
        gameGuiPage = new GameGUIPage(this);
        connect4 = new C4Page(this, gameGuiPage);
        tttPage = new TTTPage(this, gameGuiPage);
        nodes.add(gameGuiPage);
        nodes.add(connect4);
        nodes.add(tttPage);
        nodes.add(new ChooseModePage());
        nodes.add(new GameDashboardPage());
        nodes.add(new HostLobbyPage(this));
        nodes.add(new InfoPage());
        nodes.add(new JoinLobbyPage(this));
        nodes.add(new LoginPage());
        nodes.add(new MainLobbyPage(this));
        nodes.add(new NewUserPage());
        nodes.add(new ProfileDashboardPage());
        nodes.add(new SettingsPage());
    }

    public static CentralController getInstance() {
        return instance;
    }

    /** Primary app window, for modal dialogs (e.g. themed {@link javafx.scene.control.Alert}s). */
    public static Window getAppWindow() {
        if (instance == null || instance.root == null || instance.root.getScene() == null) {
            return null;
        }
        return instance.root.getScene().getWindow();
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
            GameGUIPage gameGUIPage = (GameGUIPage) getNode(Navigation.GAME_GUI);
            viewContainer.setCenter(gameGUIPage);
            gameGUIPage.setGameNode((Node) node);
            gameGUIPage.reset();
            previousScreen = Navigation.CHOOSE_MODE;
        }
        else {
            viewContainer.setCenter((Node) node);
        }

        node.reset();

        currentScreen = nav;
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
