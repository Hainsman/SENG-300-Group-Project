package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.services.LeaderBoardService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Profile dashboard: display name, per-game ELO, and local leaderboard samples.
 */
public class ProfileDashboardController {

    @FXML private TextField displayNameField;
    @FXML private TextField emailField;
    @FXML private Label statusLabel;
    @FXML private Label tttEloLabel;
    @FXML private Label tttGamesLabel;
    @FXML private Label c4EloLabel;
    @FXML private Label c4GamesLabel;
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
            tttEloLabel.setText(String.valueOf(ttt.getElo()));
        }
        if (tttGamesLabel != null) {
            tttGamesLabel.setText(ttt.getGameCounter() + " games played");
        }
        if (c4EloLabel != null) {
            c4EloLabel.setText(String.valueOf(c4.getElo()));
        }
        if (c4GamesLabel != null) {
            c4GamesLabel.setText(c4.getGameCounter() + " games played");
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
