package ca.ucalgary.seng300.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class mainLobbyController {
    // ========================
    // FXML ELEMENTS
    // ========================

    @FXML private Label lobbyTitle;

    @FXML private ListView<String> playerList;

    @FXML private Label lobbyCodeLabel;
    @FXML private Label privacyLabel;

    @FXML private Label gameNameLabel;
    @FXML private Label maxPlayersLabel;

    @FXML private Button startGameBtn;

    // ========================
    // INITIALIZE
    // ========================

    @FXML
    public void initialize() {

        if (lobbyTitle != null) {
            lobbyTitle.setText(CentralController.selectedGame + " Lobby");
        }

        if (lobbyCodeLabel != null) {
            lobbyCodeLabel.setText(
                    CentralController.lobbyCode != null ? CentralController.lobbyCode : "----"
            );
        }

        if (privacyLabel != null) {
            privacyLabel.setText(
                    CentralController.privacy != null ? CentralController.privacy : "Public"
            );
        }

        if (gameNameLabel != null) {
            gameNameLabel.setText(CentralController.selectedGame != null ? CentralController.selectedGame : "Standard");
        }

        if (maxPlayersLabel != null) {
            int maxPlayers = CentralController.maxPlayers > 0 ? CentralController.maxPlayers : 8;
            maxPlayersLabel.setText(String.valueOf(maxPlayers));
        }

        // 🔥 Dummy players (replace later)
        if (playerList != null) {
            playerList.getItems().addAll(
                    "You (Host)",
                    "Player 2",
                    "Player 3"
            );
        }

        // 🔥 Optional: disable start button if not host
        if (startGameBtn != null && !"hostLobby.fxml".equals(CentralController.previousScreen)) {
            startGameBtn.setDisable(true);
        }
    }





    @FXML
    private void handleStartGame() {

        String game = CentralController.selectedGame;

        switch (game) {

            case "Checkers":
                CentralController.moveTo("GameGUI.fxml"); // assuming this is checkers
                break;

            case "Connect 4":
                CentralController.moveTo("Connect4.fxml"); // EXACT match
                break;

            case "Tic Tac Toe":
            case "TicTacToe":
                CentralController.moveTo("TicTacToe.fxml");
                break;

            default:
                System.out.println("Unknown game: " + game);
                break;
        }
    }





    @FXML
    private void handleCopyCode() {
        System.out.println("Copied: " + CentralController.lobbyCode);

        // (optional clipboard logic later)
    }


    // BACK BUTTON

    @FXML
    private void handleBack() {

        // 🔥 Dynamic back navigation
        if (CentralController.previousScreen != null) {
            CentralController.moveTo(CentralController.previousScreen);
        } else {
            CentralController.moveTo("chooseMode.fxml");
        }
    }
}
