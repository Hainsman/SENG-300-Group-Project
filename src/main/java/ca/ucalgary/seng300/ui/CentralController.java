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
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;

import java.util.ArrayList;

// CentralController purpose is to hold the central container that each page's fxml will display in.
public class CentralController {
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
    @FXML private Slider volumeSlider;
    @FXML private Label volumeIcon;
    @FXML private Label volumePercent;

    private final ArrayList<Navigable> nodes = new ArrayList<>();

    public TTTPage tttPage;
    public C4Page connect4;

    public GameType selectedGame;
    public int maxPlayers;
    public String privacy;
    public String lobbyCode;
    /** Display name from host lobby screen (optional). */
    public String lobbyName;
    /** True if this client created the current lobby (may start the game). */
    public boolean hostingLobby;
    public Navigation previousScreen;
    public Navigation currentScreen;

    /**
     * When true, {@link ca.ucalgary.seng300.ui.games.CommonGameLogic} wires the game to a local session with a random-move AI
     * opponent. Cleared when leaving that flow or the game screen.
     */
    public boolean localAiMode = false;

    public int currentUserId;
    public String displayName = "Player";

    public String rosterHostName = "";
    public int rosterHostElo = 1200;
    public String rosterGuestName = "";
    public int rosterGuestElo = 1200;

    public void clearLobbyRoster() {
        rosterHostName = "";
        rosterGuestName = "";
        rosterHostElo = 1200;
        rosterGuestElo = 1200;
    }

    /**
     * Clears signed-in session and lobby navigation state, then shows the login screen.
     */
    public void logout() {
        currentUserId = 0;
        displayName = "Player";
        previousScreen = null;
        selectedGame = null;
        localAiMode = false;
        hostingLobby = false;
        lobbyCode = null;
        lobbyName = null;
        maxPlayers = 0;
        privacy = null;
        clearLobbyRoster();
        moveTo(Navigation.LOGIN);
    }

    public void applyLobbyRosterPayload(String payload) {
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

    private int parseEloSegment(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 1200;
        }
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
        clientCore = new ClientCore(this);
        clientCore.start();
        System.out.println("cc loaded");

        WindowManager.WindowControls controls = new WindowManager.WindowControls(
                root,
                titleBarPane,
                titleLabel,
                quitButton,
                minimizeButton,
                maximizeButton,
                volumeSlider,
                volumeIcon,
                volumePercent
        );

        windowManager = new WindowManager(controls);
        windowManager.playAudioClip(SoundEffect.APP_STARTUP);

        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                syncUiPreferencesToScene();
            }
        });

        root.addEventFilter(KeyEvent.KEY_PRESSED, this::handleGlobalKeyPress);

        initializeNodes();

        // Start at auth, then GameDashboardNew → chooseModeNew → lobby or GameGUI (incl. Play with AI).
        loadView(Navigation.LOGIN);
    }

    private void initializeNodes() {
        GameGUIPage gameGUIPage = new GameGUIPage(this);
        connect4 = new C4Page(this, gameGUIPage);
        tttPage = new TTTPage(this, gameGUIPage);
        nodes.add(gameGUIPage);
        nodes.add(connect4);
        nodes.add(tttPage);
        nodes.add(new ChooseModePage(this));
        nodes.add(new GameDashboardPage(this));
        nodes.add(new HostLobbyPage(this));
        nodes.add(new InfoPage(this));
        nodes.add(new JoinLobbyPage(this));
        nodes.add(new LoginPage(this));
        nodes.add(new MainLobbyPage(this));
        nodes.add(new NewUserPage(this));
        nodes.add(new ProfileDashboardPage(this));
        nodes.add(new SettingsPage(this));
    }

    // call anytime to switch pages
    public void moveTo(Navigation nav) {
        previousScreen = currentScreen;
        loadView(nav);
    }

    /**
     * Re-applies text scaling, window size, and colorblind root class for the whole app.
     */
    public void applyUiPreferences() {
        syncUiPreferencesToScene();
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
            gameGUIPage.setGameNode((Node) node, GameType.getFromNavigation(nav));
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

    private void handleGlobalKeyPress(KeyEvent event) {
        if (event.getCode() != KeyCode.ESCAPE) return;
        if (currentScreen == null) return;

        Navigation back = getEscapeDestination(currentScreen);
        if (back != null) {
            moveTo(back);
            event.consume();
        }
    }

    private Navigation getEscapeDestination(Navigation screen) {
        return switch (screen) {
            case CHOOSE_MODE      -> Navigation.GAME_DASHBOARD;
            case HOST_LOBBY       -> Navigation.CHOOSE_MODE;
            case JOIN_LOBBY       -> Navigation.CHOOSE_MODE;
            case MAIN_LOBBY       -> Navigation.CHOOSE_MODE;
            case SETTINGS         -> Navigation.GAME_DASHBOARD;
            case PROFILE_DASHBOARD -> Navigation.GAME_DASHBOARD;
            case INFO             -> Navigation.GAME_DASHBOARD;
            default               -> null;  // no Escape on login, game, dashboard
        };
    }

    public boolean isGameOn() {
        return currentScreen == Navigation.C4 || currentScreen == Navigation.TTT;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }
}
