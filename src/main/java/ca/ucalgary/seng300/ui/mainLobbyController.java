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

        lobbyTitle.setText(CentralController.selectedGame + " Lobby");

        lobbyCodeLabel.setText(
                CentralController.lobbyCode != null ? CentralController.lobbyCode : "----"
        );

        privacyLabel.setText(
                CentralController.privacy != null ? CentralController.privacy : "Public"
        );

        gameNameLabel.setText("Game: " + CentralController.selectedGame);

        maxPlayersLabel.setText(
                "Max Players: " + CentralController.maxPlayers
        );

        // 🔥 Dummy players (replace later)
        playerList.getItems().addAll(
                "You (Host)",
                "Player 2",
                "Player 3"
        );

        // 🔥 Optional: disable start button if not host
        if (!"hostLobby.fxml".equals(CentralController.previousScreen)) {
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
