package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainLobby extends AnchorPane implements Navigable {

    @FXML private Label lobbyTitle;
    @FXML private ListView<String> playerList;
    @FXML private Label lobbyCodeLabel;
    @FXML private Label privacyLabel;
    @FXML private Label gameNameLabel;
    @FXML private Label maxPlayersLabel;
    @FXML private Label playerCountLabel;
    @FXML private Button startGameBtn;
    @FXML private Button copyCodeBtn;

    public MainLobby() {
        try {
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
                CentralController.lobbyCode != null ? CentralController.lobbyCode : "----"
        );

        privacyLabel.setText(
                CentralController.privacy != null ? CentralController.privacy : "Public"
        );

        gameNameLabel.setText(CentralController.selectedGame != null ? CentralController.selectedGame.toString() : "Standard");

        int max = CentralController.maxPlayers > 0 ? CentralController.maxPlayers : 2;
        maxPlayersLabel.setText(String.valueOf(max));

        refreshPlayersFromState();
        refreshStartButton();

        LobbyBridge.onPartnerJoined = this::refreshPlayersFromState;
        LobbyBridge.onGameState = this::refreshStartButton;
    }

    private void refreshPlayersFromState() {
        if (playerList != null) {
            playerList.getItems().clear();
            ClientCore cc = CentralController.getInstance() != null ? CentralController.getInstance().getCC() : null;
            boolean guestThere = cc != null && cc.getLastUpdate() != null;
            if (CentralController.hostingLobby) {
                playerList.getItems().add("You (host)");
                playerList.getItems().add(guestThere ? "Guest (connected)" : "Waiting for guest…");
            } else {
                playerList.getItems().add(guestThere ? "Host (connected)" : "Host");
                playerList.getItems().add("You (guest)");
            }
        }
        if (playerCountLabel != null) {
            ClientCore cc = CentralController.getInstance() != null ? CentralController.getInstance().getCC() : null;
            boolean paired = cc != null && cc.getLastUpdate() != null;
            int cur = paired ? 2 : 1;
            int max = CentralController.maxPlayers > 0 ? CentralController.maxPlayers : 2;
            playerCountLabel.setText("Players (" + cur + "/" + max + ")");
        }
    }

    private void refreshStartButton() {
        if (startGameBtn == null) {
            return;
        }
        ClientCore cc = CentralController.getInstance() != null ? CentralController.getInstance().getCC() : null;
        boolean paired = cc != null && cc.getLastUpdate() != null;
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
            CentralController c = CentralController.getInstance();
            if (c != null) {
                ClientCore cc = c.getCC();
                if (cc != null && cc.isConnected()) {
                    cc.msgSend(new sysMSG("GMJN", nav.getFxml(), ""));
                }
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
