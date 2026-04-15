package ca.ucalgary.seng300.ui.miscellaneous;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.ui.centralcontroller.CentralController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ChooseModeController {

    @FXML
    private Label gameTitleLabel;

    @FXML
    private Label aiOpponentLabel;

    @FXML
    public void initialize() {

        String title = CentralController.selectedGame != null ? CentralController.selectedGame.toString() : "";
        gameTitleLabel.setText(title);
        aiOpponentLabel.setText("Random opponent — " + title);

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
        GameType g = CentralController.selectedGame;
        if (g == GameType.CONNECT4) {
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
    void handleBack(MouseEvent event) {
        CentralController.moveTo("GameDashboardNew.fxml");
    }

    @FXML
    void handleLogout(MouseEvent event) {
        if (CentralController.getInstance().clientCore != null) {
            CentralController.getInstance().clientCore.sendLogout();
        }
        CentralController.moveTo("Login.fxml");
    }

    @FXML
    void handleProfileMenuProfile(ActionEvent event) {
        CentralController.previousScreen = "chooseModeNew.fxml";
        CentralController.moveTo("ProfileDashboard.fxml");
    }

    @FXML
    void handleProfileMenuSettings(ActionEvent event) {
        CentralController.previousScreen = "chooseModeNew.fxml";
        CentralController.moveTo("Settings.fxml");
    }

    @FXML
    void handleProfileMenuLogout(ActionEvent event) {
        handleLogout(null);
    }
}
