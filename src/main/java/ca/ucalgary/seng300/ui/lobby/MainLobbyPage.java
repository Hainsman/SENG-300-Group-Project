package ca.ucalgary.seng300.ui.lobby;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import ca.ucalgary.seng300.ui.util.PlayerProfileHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainLobbyPage extends AnchorPane implements Navigable {

    private CentralController rootController;

    @FXML private Label lobbyTitle;
    @FXML private Label lobbySubtitle;
    @FXML private Label lobbyCodeLabel;
    @FXML private Label privacyLabel;
    @FXML private Label gameNameLabel;
    @FXML private Label maxPlayersLabel;
    @FXML private Label playerCountLabel;
    @FXML private Label hostNameLabel;
    @FXML private Label hostEloLabel;
    @FXML private Label guestNameLabel;
    @FXML private Label guestEloLabel;
    @FXML private Label hostYouPill;
    @FXML private Label guestYouPill;
    @FXML private Label connectionHintLabel;
    @FXML private Button startGameBtn;
    @FXML private Button copyCodeBtn;

    public MainLobbyPage(CentralController rootController) {
        try {
            this.rootController = rootController;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ca.ucalgary.seng300.ui/" + getNavigation().getFxml()));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        applyGameAccent();
        GameType g = CentralController.selectedGame != null ? CentralController.selectedGame : GameType.TIC_TAC_TOE;
        String gameTitle = g.toString();
        lobbyTitle.setText(gameTitle + " · Lobby");
        lobbySubtitle.setText(
                "Share the lobby code with your opponent. When both players are connected, the host can start the match.");

        lobbyCodeLabel.setText(
                CentralController.lobbyCode != null ? CentralController.lobbyCode : "----"
        );

        privacyLabel.setText(
                CentralController.privacy != null ? CentralController.privacy : "Public"
        );

        gameNameLabel.setText(gameTitle);

        int max = CentralController.maxPlayers > 0 ? CentralController.maxPlayers : 2;
        maxPlayersLabel.setText(String.valueOf(max));

        refreshPlayersFromState();
        refreshStartButton();

        LobbyBridge.onPartnerJoined = this::refreshPlayersFromState;
        LobbyBridge.onGameState = this::refreshStartButton;
    }

    private void applyGameAccent() {
        getStyleClass().removeAll("lobby-ttt", "lobby-c4");
        if (CentralController.selectedGame == GameType.CONNECT4) {
            getStyleClass().add("lobby-c4");
        } else {
            getStyleClass().add("lobby-ttt");
        }
    }

    private boolean isPaired() {
        return rosterPopulated();
    }

    private boolean rosterPopulated() {
        String h = CentralController.rosterHostName;
        String guest = CentralController.rosterGuestName;
        return h != null && !h.isBlank() && guest != null && !guest.isBlank();
    }

    private String currentUserDisplayName() {
        int uid = CentralController.currentUserId;
        if (uid > 0) {
            User u = UserStore.getInstance().get(uid);
            if (u != null && u.getlogin() != null && !u.getlogin().isBlank()) {
                return u.getlogin().trim();
            }
        }
        if (CentralController.displayName != null && !CentralController.displayName.isBlank()) {
            return CentralController.displayName.trim();
        }
        return "Player";
    }

    private int myEloForSelectedGame() {
        GameType g = CentralController.selectedGame != null ? CentralController.selectedGame : GameType.TIC_TAC_TOE;
        int uid = CentralController.currentUserId;
        if (uid > 0) {
            User u = UserStore.getInstance().get(uid);
            if (u != null) {
                return u.getEloForGame(g);
            }
        }
        return PlayerProfileHelper.eloForGameType(g);
    }

    private void setYouPills(boolean hosting) {
        boolean showHost = hosting;
        boolean showGuest = !hosting;
        if (hostYouPill != null) {
            hostYouPill.setVisible(showHost);
            hostYouPill.setManaged(showHost);
        }
        if (guestYouPill != null) {
            guestYouPill.setVisible(showGuest);
            guestYouPill.setManaged(showGuest);
        }
    }

    private void refreshPlayersFromState() {
        boolean hosting = CentralController.hostingLobby;
        boolean paired = isPaired();
        boolean roster = paired;

        setYouPills(hosting);

        String hostName;
        String guestName;
        int hostElo;
        int guestElo;

        if (roster) {
            hostName = CentralController.rosterHostName.trim();
            guestName = CentralController.rosterGuestName.trim();
            hostElo = CentralController.rosterHostElo;
            guestElo = CentralController.rosterGuestElo;
        } else if (hosting) {
            hostName = currentUserDisplayName();
            hostElo = myEloForSelectedGame();
            guestName = paired ? "Opponent" : "Waiting for opponent…";
            guestElo = paired ? Math.max(0, CentralController.rosterGuestElo) : -1;
        } else {
            guestName = currentUserDisplayName();
            guestElo = myEloForSelectedGame();
            hostName = paired ? "Host" : "Waiting…";
            hostElo = paired ? Math.max(0, CentralController.rosterHostElo) : -1;
        }

        if (hostNameLabel != null) {
            hostNameLabel.setText(hostName);
        }
        if (guestNameLabel != null) {
            guestNameLabel.setText(guestName);
        }
        if (hostEloLabel != null) {
            hostEloLabel.setText(hostElo >= 0 ? ("Elo " + hostElo) : "Elo —");
        }
        if (guestEloLabel != null) {
            guestEloLabel.setText(guestElo >= 0 ? ("Elo " + guestElo) : "Elo —");
        }

        if (connectionHintLabel != null) {
            if (paired) {
                connectionHintLabel.setText("Both players connected. Host can start when ready.");
                connectionHintLabel.setVisible(true);
                connectionHintLabel.setManaged(true);
            } else if (!paired) {
                connectionHintLabel.setText("Waiting for your opponent to join…");
                connectionHintLabel.setVisible(true);
                connectionHintLabel.setManaged(true);
            } else {
                connectionHintLabel.setText("Syncing player info…");
                connectionHintLabel.setVisible(true);
                connectionHintLabel.setManaged(true);
            }
        }

        if (playerCountLabel != null) {
            int cur = paired ? 2 : 1;
            int max = CentralController.maxPlayers > 0 ? CentralController.maxPlayers : 2;
            playerCountLabel.setText("Players · " + cur + " / " + max);
        }
    }

    private void refreshStartButton() {
        if (startGameBtn == null) {
            return;
        }
        boolean paired = isPaired();
        boolean host = CentralController.hostingLobby;
        startGameBtn.setDisable(!host || !paired);
        refreshPlayersFromState();
    }

    @FXML
    private void handleStartGame() {
        GameType game = CentralController.selectedGame;
        Navigation nav = GameType.getNavigation(game);

        if (nav == null) {
            System.out.println("Unknown game: ");
            return;
        }

        CentralController.ticTacToeLocalAiMode = false;
        CentralController.connect4LocalAiMode = false;
        if (CentralController.hostingLobby) {
            ClientCore cc = rootController.getCC();
            if (cc != null && cc.isConnected()) {
                cc.msgSend(new sysMSG("GMJN", nav.getFxml(), ""));
            }
        }
        LobbyBridge.clearLobbyUiCallbacks();
        CentralController.moveTo(nav);
    }

    @FXML
    private void handleCopyCode() {
        if (CentralController.lobbyCode == null || CentralController.lobbyCode.isBlank()) {
            return;
        }
        ClipboardContent content = new ClipboardContent();
        content.putString(CentralController.lobbyCode);
        Clipboard.getSystemClipboard().setContent(content);
    }

    @FXML
    private void handleBack() {
        LobbyBridge.clearLobbyUiCallbacks();
        if (Navigation.HOST_LOBBY == CentralController.previousScreen
                || Navigation.JOIN_LOBBY == CentralController.previousScreen) {
            CentralController.moveTo(Navigation.GAME_DASHBOARD);
            return;
        }
        if (CentralController.previousScreen != null) {
            CentralController.moveTo(CentralController.previousScreen);
        } else {
            CentralController.moveTo(Navigation.GAME_DASHBOARD);
        }
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.MAIN_LOBBY;
    }

    @Override
    public void reset() {
        initialize();
    }
}
