package ca.ucalgary.seng300.ui.lobby;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.ui.centralcontroller.CentralController;
import ca.ucalgary.seng300.ui.SoundEffect;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import ca.ucalgary.seng300.ui.util.PlayerProfileHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainLobbyPage extends AnchorPane implements Navigable {

    private CentralController rootController;

    @FXML private Label lobbyTitle;
    @FXML private ListView<String> playerList;
    @FXML private Label lobbyCodeLabel;
    @FXML private Label privacyLabel;
    @FXML private Label gameNameLabel;
    @FXML private Label maxPlayersLabel;
    @FXML private Label playerCountLabel;
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
        String g = CentralController.selectedGame != null ? CentralController.selectedGame.toString() : "Game";
        lobbyTitle.setText(g + " Lobby");

        lobbyCodeLabel.setText(
                rootController.lobbyCode != null ? rootController.lobbyCode : "----"
        );

        privacyLabel.setText(
                rootController.privacy != null ? rootController.privacy : "Public"
        );

        gameNameLabel.setText(CentralController.selectedGame != null ? CentralController.selectedGame.toString() : "Standard");

        int max = rootController.maxPlayers > 0 ? rootController.maxPlayers : 2;
        maxPlayersLabel.setText(String.valueOf(max));

        refreshPlayersFromState();
        refreshStartButton();

        //LobbyBridge.onPartnerJoined = this::refreshPlayersFromState;
        //LobbyBridge.onGameState = this::refreshStartButton;
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
        String h = rootController.rosterHostName;
        String guest = rootController.rosterGuestName;
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
        return PlayerProfileHelper.eloForGameType(g, rootController);
    }
    /*
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
    boolean paired = isPaired();
        boolean roster = paired;

        setYouPills(hosting);

        String hostName;
        String guestName;
        int hostElo;
        int guestElo;

        if (roster) {
            hostName = rootController.rosterHostName.trim();
            guestName = rootController.rosterGuestName.trim();
            hostElo = rootController.rosterHostElo;
            guestElo = rootController.rosterGuestElo;
        } else if (hosting) {
            hostName = currentUserDisplayName();
            hostElo = myEloForSelectedGame();
            guestName = paired ? "Opponent" : "Waiting for opponent…";
            guestElo = paired ? Math.max(0, rootController.rosterGuestElo) : -1;
        } else {
            guestName = currentUserDisplayName();
            guestElo = myEloForSelectedGame();
            hostName = paired ? "Host" : "Waiting…";
            hostElo = paired ? Math.max(0, rootController.rosterHostElo) : -1;
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
    */

    public void refreshPlayersFromState() {
        System.out.println(" player update triggered");
        if (playerList != null) {
            playerList.getItems().clear();
            ClientCore cc = rootController != null ? rootController.getCC() : null;
            boolean guestThere = cc.otherjoined;
            if (rootController.hostingLobby) {
                playerList.getItems().add("You (host)");
                playerList.getItems().add(guestThere ? "Guest (connected)" : "Waiting for guest…");
            } else {
                playerList.getItems().add(guestThere ? "Host (connected)" : "Host");
                playerList.getItems().add("You (guest)");
            }
        }
        if (playerCountLabel != null) {
            ClientCore cc = rootController != null ? rootController.getCC() : null;
            boolean paired = cc.otherjoined;
            int cur = paired ? 2 : 1;
            int max = rootController.maxPlayers > 0 ? rootController.maxPlayers : 2;
            playerCountLabel.setText("Players (" + cur + "/" + max + ")");
        }
        refreshStartButton();
    }

    public void refreshStartButton() {
        if (startGameBtn == null) {
            return;
        }
        ClientCore cc = rootController != null ? rootController.getCC() : null;
        boolean paired = cc.otherjoined;
        boolean host = rootController.hostingLobby;
        startGameBtn.setDisable(!host || !paired);
        System.out.println(" start button updated pair"+ paired + " host"+ host);
    }

    @FXML
    private void handleStartGame() {
        GameType game = CentralController.selectedGame;
        Navigation nav = GameType.getNavigation(game);

        if (nav == null) {
            System.out.println("Unknown game: ");
            return;
        }

        CentralController.playWithAi = false;
        if (rootController.hostingLobby) {
            ClientCore cc = rootController.getCC();
            if (cc != null && cc.isConnected()) {
                System.out.println("start lobby");
                cc.msgSend(new sysMSG("START_LOBBY",""));
            }
        }
    }

    public void gameStart(){
        GameType game = CentralController.selectedGame;
        Navigation nav = GameType.getNavigation(game);
        LobbyBridge.clearLobbyUiCallbacks();
        if (nav != null) {
            rootController.moveTo(nav);
        }
    }

    @FXML
    private void handleCopyCode() {
        if (rootController.lobbyCode == null || rootController.lobbyCode.isBlank()) {
            return;
        }
        ClipboardContent content = new ClipboardContent();
        content.putString(rootController.lobbyCode);
        Clipboard.getSystemClipboard().setContent(content);
    }

    @FXML
    private void handleBack() {
        LobbyBridge.clearLobbyUiCallbacks();
        String prev = CentralController.previousScreen;
        if (Navigation.HOST_LOBBY.getFxml().equals(prev)
                || Navigation.JOIN_LOBBY.getFxml().equals(prev)) {
            rootController.moveTo(Navigation.GAME_DASHBOARD);
            return;
        }
        if (prev != null) {
            CentralController.moveTo(prev);
        } else {
            rootController.moveTo(Navigation.GAME_DASHBOARD);
        }
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.MAIN_LOBBY;
    }

    @Override
    public void reset() {
        initialize();
        rootController.getWindowManager().playAudioClip(SoundEffect.WAITING_LOBBY, true);
    }
}
