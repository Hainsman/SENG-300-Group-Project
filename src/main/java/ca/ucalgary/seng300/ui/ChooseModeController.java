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

    private void refreshAiCardVisibility() {
        boolean ttt = isTicTacToeSelection();
        if (playWithAiCard != null) {
            playWithAiCard.setVisible(ttt);
            playWithAiCard.setManaged(ttt);
        }
    }

    @FXML
    void handleHostGame(MouseEvent event) {
        CentralController.ticTacToeLocalAiMode = false;
        CentralController.moveTo("hostLobby.fxml");
    }

    @FXML
    void handleJoinGame(MouseEvent event) {
        CentralController.ticTacToeLocalAiMode = false;
        CentralController.moveTo("joinLobby.fxml");
    }

    @FXML
    void handlePlayWithAi(MouseEvent event) {
        if (!isTicTacToeSelection()) {
            return;
        }
        CentralController.ticTacToeLocalAiMode = true;
        CentralController.previousScreen = "chooseModeNew.fxml";
        CentralController.moveTo(CentralController.TIC_TAC_TOE_SHELL_FXML);
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
