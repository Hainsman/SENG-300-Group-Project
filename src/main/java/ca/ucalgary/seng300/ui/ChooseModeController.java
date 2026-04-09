package ca.ucalgary.seng300.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ChooseModeController {

    @FXML
    private Label gameTitleLabel;

    @FXML
    public void initialize() {

        gameTitleLabel.setText(CentralController.selectedGame);

    }

    @FXML
    void handleHostGame(MouseEvent event) {
        CentralController.playWithAi = false;
        CentralController.moveTo("hostLobby.fxml");
    }

    @FXML
    void handleJoinGame(MouseEvent event) {
        CentralController.playWithAi = false;
        CentralController.moveTo("joinLobby.fxml");
    }

    @FXML
    void handlePlayWithAi(MouseEvent event) {
        CentralController.playWithAi = true;
        String game = CentralController.selectedGame != null ? CentralController.selectedGame : "";
        if (game.toLowerCase().contains("connect")) {
            CentralController.moveTo("Connect4.fxml");
            return;
        }
        CentralController.moveTo("TicTacToe.fxml");
    }

    @FXML
    void handleOpenLeaderboard(MouseEvent event) {
        CentralController.moveTo("Leaderboard.fxml");
    }

    @FXML
    void handleOpenSettings(MouseEvent event) {
        CentralController.previousScreen = "chooseModeNew.fxml";
        CentralController.moveTo("Settings.fxml");
    }

    @FXML
    void handleBack(MouseEvent event) {
        CentralController.moveTo("GameDashboardNew.fxml");
    }
}
