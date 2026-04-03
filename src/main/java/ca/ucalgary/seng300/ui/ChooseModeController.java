package ca.ucalgary.seng300.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ChooseModeController {

    @FXML
    private Label gameTitleLabel;

    @FXML
    private VBox playWithAiCard;

    @FXML
    public void initialize() {
        gameTitleLabel.setText(CentralController.selectedGame);
        refreshAiCardVisibility();
    }

    /** Tic-Tac-Toe from the new dashboard uses {@code "TicTacToe"}. */
    private static boolean isTicTacToeSelection() {
        String g = CentralController.selectedGame;
        return g != null && ("TicTacToe".equals(g) || "Tic Tac Toe".equalsIgnoreCase(g.trim()));
    }

    private static boolean isConnectFourSelection() {
        String g = CentralController.selectedGame;
        if (g == null) {
            return false;
        }
        String t = g.trim();
        return "Connect 4".equalsIgnoreCase(t) || "Connect4".equalsIgnoreCase(t);
    }

    private void refreshAiCardVisibility() {
        boolean showAi = isTicTacToeSelection() || isConnectFourSelection();
        if (playWithAiCard != null) {
            playWithAiCard.setVisible(showAi);
            playWithAiCard.setManaged(showAi);
        }
    }

    @FXML
    void handleHostGame(MouseEvent event) {
        CentralController.ticTacToeLocalAiMode = false;
        CentralController.connect4LocalAiMode = false;
        CentralController.moveTo("hostLobby.fxml");
    }

    @FXML
    void handleJoinGame(MouseEvent event) {
        CentralController.ticTacToeLocalAiMode = false;
        CentralController.connect4LocalAiMode = false;
        CentralController.moveTo("joinLobby.fxml");
    }

    @FXML
    void handlePlayWithAi(MouseEvent event) {
        CentralController.previousScreen = "chooseModeNew.fxml";
        if (isTicTacToeSelection()) {
            CentralController.connect4LocalAiMode = false;
            CentralController.ticTacToeLocalAiMode = true;
            CentralController.moveTo(CentralController.TIC_TAC_TOE_SHELL_FXML);
            return;
        }
        if (isConnectFourSelection()) {
            CentralController.ticTacToeLocalAiMode = false;
            CentralController.connect4LocalAiMode = true;
            CentralController.moveTo("Connect4.fxml");
            return;
        }
    }

    @FXML
    void handleProfileDashboard(ActionEvent event) {
        CentralController.previousScreen = "chooseModeNew.fxml";
        CentralController.moveTo(CentralController.PROFILE_DASHBOARD_FXML);
    }

    @FXML
    void handleTeamSettings(ActionEvent event) {
        CentralController.previousScreen = "chooseModeNew.fxml";
        CentralController.moveTo("Settings.fxml");
    }

    @FXML
    void handleBack(MouseEvent event) {
        CentralController.moveTo("GameDashboardNew.fxml");
    }
}
