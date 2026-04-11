package ca.ucalgary.seng300.ui.lobby;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.ui.centralcontroller.CentralController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class mainLobbyController {

    @FXML private Label lobbyTitle;
    @FXML private ListView<String> playerList;
    @FXML private ListView<String> friendsList;
    @FXML private Label lobbyCodeLabel;
    @FXML private Label privacyLabel;
    @FXML private Label gameNameLabel;
    @FXML private Label maxPlayersLabel;
    @FXML private Label playerCountLabel;
    @FXML private Label playerOneLabel;
    @FXML private Label playerTwoLabel;
    @FXML private Button startGameBtn;

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
        LobbyBridge.onLobbyRoster = this::handleLobbyRoster;
        LobbyBridge.onPlayerStatsUpdated = this::handlePlayerStatsUpdate;
        LobbyBridge.onChatNotification = this::handleChatNotification;
        
        ClientCore cc = getCC();
        if (cc != null) {
            cc.setFriendsListHandler(this::handleFriendsList);
            cc.msgSend(new sysMSG("GET_FRIENDS", ""));
        }

        requestRosterUpdate();
    }

    private void handleFriendsList(sysMSG msg) {
        if ("PASS".equals(msg.getFieldA()) && msg.getFieldB() != null) {
            String[] friends = msg.getFieldB().split(",");
            javafx.application.Platform.runLater(() -> {
                friendsList.getItems().clear();
                for (String friend : friends) {
                    if (!friend.isEmpty()) {
                        friendsList.getItems().add(friend);
                    }
                }
            });
        }
    }

    private ClientCore getCC() {
        CentralController controller = CentralController.getInstance();
        if (controller != null) {
            return controller.getCC();
        }
        return null;
    }

    private void refreshPlayersFromState() {
        if (playerList != null) {
            playerList.getItems().clear();
            boolean guestThere = CentralController.lobbyPaired;
            String selfName = getLoggedInUsername();
            if (CentralController.hostingLobby) {
                playerList.getItems().add("Player 1 (host): " + selfName);
                String guest2Name = CentralController.player2Name != null && !CentralController.player2Name.isEmpty() 
                    ? CentralController.player2Name 
                    : "Guest";
                playerList.getItems().add(guestThere ? "Player 2 (guest): " + guest2Name : "Player 2 (guest): Waiting for guest...");
            } else {
                String host1Name = CentralController.player1Name != null && !CentralController.player1Name.isEmpty()
                    ? CentralController.player1Name
                    : "Host";
                playerList.getItems().add("Player 1 (host): " + host1Name);
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
                String p1Display = selfName;
                if (CentralController.player1Elo != null && !CentralController.player1Elo.isEmpty()) {
                    p1Display += " (ELO: " + CentralController.player1Elo + ")";
                }
                playerOneLabel.setText(p1Display);
            }
            if (playerTwoLabel != null) {
                String p2Display;
                if (guestThere) {
                    p2Display = CentralController.player2Name != null && !CentralController.player2Name.isEmpty() 
                        ? CentralController.player2Name 
                        : "Guest";
                    if (CentralController.player2Elo != null && !CentralController.player2Elo.isEmpty()) {
                        p2Display += " (ELO: " + CentralController.player2Elo + ")";
                    }
                } else {
                    p2Display = "Waiting for guest...";
                }
                playerTwoLabel.setText(p2Display);
            }
            return;
        }

        if (playerOneLabel != null) {
            String p1Display = CentralController.player1Name != null && !CentralController.player1Name.isEmpty()
                ? CentralController.player1Name
                : "Host";
            if (CentralController.player1Elo != null && !CentralController.player1Elo.isEmpty()) {
                p1Display += " (ELO: " + CentralController.player1Elo + ")";
            }
            playerOneLabel.setText(p1Display);
        }
        if (playerTwoLabel != null) {
            String p2Display = selfName;
            if (CentralController.player2Elo != null && !CentralController.player2Elo.isEmpty()) {
                p2Display += " (ELO: " + CentralController.player2Elo + ")";
            }
            playerTwoLabel.setText(p2Display);
        }
    }

    private void handleLobbyRoster(String rosterData) {
        System.out.println("handleLobbyRoster called with data: " + rosterData);
        if (rosterData == null || rosterData.isEmpty()) {
            System.out.println("handleLobbyRoster: data is null or empty");
            return;
        }
        String[] parts = rosterData.split("\\|", -1);
        System.out.println("handleLobbyRoster: parsed " + parts.length + " parts");
        if (parts.length >= 6) {
            CentralController.player1Name = parts[0];
            CentralController.player1Elo = parts[1];
            CentralController.player1GamesPlayed = parts[2];
            CentralController.player2Name = parts[3];
            CentralController.player2Elo = parts[4];
            CentralController.player2GamesPlayed = parts[5];
            
            // If player 2 name is empty, it means the guest has left (or is not yet present)
            if (CentralController.player2Name == null || CentralController.player2Name.isEmpty()) {
                CentralController.lobbyPaired = false;
            } else {
                CentralController.lobbyPaired = true;
            }

            System.out.println("handleLobbyRoster: set p1=" + parts[0] + " elo=" + parts[1] + " games=" + parts[2] 
                + " p2=" + parts[3] + " elo=" + parts[4] + " games=" + parts[5]);
            refreshPlayerSlots();
            refreshPlayersFromState();
            refreshStartButton();
        } else {
            System.out.println("handleLobbyRoster: not enough parts, expected 6, got " + parts.length);
        }
    }

    private void handleChatNotification(String message) {
        if (message == null || message.isBlank()) return;
        
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Lobby Notification");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            
            if (message.contains("Host has left")) {
                handleBack();
            }
        });
    }

    private void handlePlayerStatsUpdate(String statsData) {
        if (statsData == null || statsData.isEmpty()) {
            return;
        }
        String[] parts = statsData.split("\\|");
        if (parts.length >= 1) {
            String newScore = parts[0];
            String myUsername = getLoggedInUsername();
            if (myUsername != null) {
                if (CentralController.hostingLobby && myUsername.equals(CentralController.player1Name)) {
                    CentralController.player1Elo = newScore;
                } else if (!CentralController.hostingLobby && myUsername.equals(CentralController.player2Name)) {
                    CentralController.player2Elo = newScore;
                }
            }
            refreshPlayerSlots();
        }
    }

    private void requestRosterUpdate() {
        CentralController controller = CentralController.getInstance();
        if (controller != null) {
            ClientCore cc = controller.getCC();
            if (cc != null && cc.isConnected()) {
                cc.msgSend(new sysMSG("ROSTER_REQUEST", CentralController.lobbyCode, ""));
            }
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
        CentralController controller = CentralController.getInstance();
        if (controller != null) {
            ClientCore cc = controller.getCC();
            if (cc != null && cc.isConnected()) {
                cc.msgSend(new sysMSG("LEAVE_LOBBY", CentralController.lobbyCode != null ? CentralController.lobbyCode : ""));
            }
        }
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
