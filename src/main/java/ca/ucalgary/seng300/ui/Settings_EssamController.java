package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.services.LeaderBoardService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Profile / stats / ELO UI (Essam branch) — separate name from team {@code Settings} to reduce merge conflicts.
 */
public class Settings_EssamController {
    @FXML private TextField displayNameField;
    @FXML private TextField emailField;
    @FXML private Label statusLabel;
    @FXML private Label tttEloLabel;
    @FXML private Label c4EloLabel;
    @FXML private Label tierSummaryLabel;
    @FXML private ListView<String> leaderboardList;

    @FXML
    private void initialize() {
        if (displayNameField != null) {
            displayNameField.setText(
                    CentralController.displayName != null ? CentralController.displayName : "Player");
        }
        if (emailField != null) {
            emailField.setText("player@example.com");
        }
        refreshStats();
    }

    private void refreshStats() {
        var ttt = PlayerProfileHelper.statsForGameId(Game.TICK_TACK_TOE);
        var c4 = PlayerProfileHelper.statsForGameId(Game.CONNECT_FOUR);
        if (tttEloLabel != null) {
            tttEloLabel.setText(String.format(
                    "Tic Tac Toe — %d ELO  ·  %d games", ttt.getElo(), ttt.getGameCounter()));
        }
        if (c4EloLabel != null) {
            c4EloLabel.setText(String.format(
                    "Connect 4 — %d ELO  ·  %d games", c4.getElo(), c4.getGameCounter()));
        }
        if (tierSummaryLabel != null) {
            tierSummaryLabel.setText(String.format(
                    "Tier labels: TTT %s  ·  C4 %s",
                    PlayerProfileHelper.eloTierLabel(ttt.getElo()),
                    PlayerProfileHelper.eloTierLabel(c4.getElo())));
        }
        if (leaderboardList != null) {
            leaderboardList.getItems().clear();
            leaderboardList.getItems().add("— Tic Tac Toe (local) —");
            appendLeaderboardRows(Game.TICK_TACK_TOE);
            leaderboardList.getItems().add("— Connect 4 (local) —");
            appendLeaderboardRows(Game.CONNECT_FOUR);
        }
    }

    private void appendLeaderboardRows(int gameId) {
        var top = LeaderBoardService.getInstance().getLeaderBoard(true, gameId, 6);
        if (top.isEmpty()) {
            leaderboardList.getItems().add("  (no rows yet)");
            return;
        }
        for (int i = 0; i < top.size(); i++) {
            var p = top.get(i);
            leaderboardList.getItems().add(
                    "  #" + (i + 1) + "  user " + p.getUserID() + " — " + p.getElo() + " ELO");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (displayNameField != null) {
            CentralController.displayName = displayNameField.getText().trim();
            if (CentralController.displayName.isEmpty()) {
                CentralController.displayName = "Player";
            }
        }
        if (statusLabel != null) {
            statusLabel.setText("Profile updated.");
        }
        refreshStats();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (CentralController.previousScreen != null && !CentralController.previousScreen.isBlank()) {
            CentralController.moveTo(CentralController.previousScreen);
            return;
        }
        CentralController.moveTo("GameDashboardNew.fxml");
    }
}
