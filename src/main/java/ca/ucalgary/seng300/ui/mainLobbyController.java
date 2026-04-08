package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class mainLobbyController {

    @FXML private Label lobbyTitle;
    @FXML private ListView<String> playerList;
    @FXML private Label lobbyCodeLabel;
    @FXML private Label privacyLabel;
    @FXML private Label gameNameLabel;
    @FXML private Label maxPlayersLabel;
    @FXML private Label playerCountLabel;
    @FXML private Label playerOneLabel;
    @FXML private Label playerTwoLabel;
    @FXML private Button startGameBtn;
    @FXML private Button copyCodeBtn;

    @FXML
    public void initialize() {
        if (lobbyTitle != null) {
            String game = CentralController.selectedGame != null ? CentralController.selectedGame : "Game";
            lobbyTitle.setText(game + " Lobby");
        }

        if (lobbyCodeLabel != null) {
            lobbyCodeLabel.setText(CentralController.lobbyCode != null ? CentralController.lobbyCode : "----");
        }

        if (privacyLabel != null) {
            privacyLabel.setText(CentralController.privacy != null ? CentralController.privacy : "Private");
        }

        if (gameNameLabel != null) {
            gameNameLabel.setText(CentralController.selectedGame != null ? CentralController.selectedGame : "Standard");
        }

        if (maxPlayersLabel != null) {
            int max = CentralController.maxPlayers > 0 ? CentralController.maxPlayers : 2;
            maxPlayersLabel.setText(String.valueOf(max));
        }

        refreshPlayerSlots();
        refreshPlayersFromState();
        refreshStartButton();

        LobbyBridge.onPartnerJoined = this::refreshStartButton;
        LobbyBridge.onGameState = this::refreshStartButton;
    }

    private void refreshPlayersFromState() {
        if (playerList != null) {
            playerList.getItems().clear();
            boolean guestThere = CentralController.lobbyPaired;
            String selfName = getLoggedInUsername();
            if (CentralController.hostingLobby) {
                playerList.getItems().add("Player 1 (host): " + selfName);
                playerList.getItems().add(guestThere ? "Player 2 (guest): Guest" : "Player 2 (guest): Waiting for guest...");
            } else {
                playerList.getItems().add("Player 1 (host): Host");
                playerList.getItems().add("Player 2 (guest): " + selfName);
            }
        }
        if (playerCountLabel != null) {
            boolean paired = CentralController.lobbyPaired;
            int cur = paired ? 2 : 1;
            int max = CentralController.maxPlayers > 0 ? CentralController.maxPlayers : 2;
            playerCountLabel.setText("Players (" + cur + "/" + max + ")");
        }
        refreshPlayerSlots();
    }

    private void refreshStartButton() {
        if (startGameBtn == null) {
            return;
        }
        boolean paired = CentralController.lobbyPaired;
        boolean host = CentralController.hostingLobby;
        startGameBtn.setDisable(!host || !paired);
        refreshPlayersFromState();
    }

    private String getLoggedInUsername() {
        CentralController controller = CentralController.getInstance();
        if (controller != null) {
            ClientCore cc = controller.getCC();
            if (cc != null && cc.username != null && !cc.username.isBlank()) {
                return cc.username;
            }
        }
        return "Player";
    }

    private void refreshPlayerSlots() {
        String selfName = getLoggedInUsername();
        boolean guestThere = CentralController.lobbyPaired;

        if (CentralController.hostingLobby) {
            if (playerOneLabel != null) {
                playerOneLabel.setText(selfName);
            }
            if (playerTwoLabel != null) {
                playerTwoLabel.setText(guestThere ? "Guest" : "Waiting for guest...");
            }
            return;
        }

        if (playerOneLabel != null) {
            playerOneLabel.setText("Host");
        }
        if (playerTwoLabel != null) {
            playerTwoLabel.setText(selfName);
        }
    }

    @FXML
    private void handleStartGame() {
        if (CentralController.hostingLobby) {
            CentralController controller = CentralController.getInstance();
            if (controller != null) {
                ClientCore cc = controller.getCC();
                if (cc != null && cc.isConnected()) {
                    cc.msgSend(new sysMSG("CREATE_GAME", CentralController.lobbyCode, ""));
                }
            }
        }
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
        if ("hostLobby.fxml".equals(CentralController.previousScreen)
                || "joinLobby.fxml".equals(CentralController.previousScreen)) {
            CentralController.moveTo("GameDashboardNew.fxml");
            return;
        }
        if (CentralController.previousScreen != null) {
            CentralController.moveTo(CentralController.previousScreen);
        } else {
            CentralController.moveTo("GameDashboardNew.fxml");
        }
    }
}
