package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.services.LeaderBoardService;
import ca.ucalgary.seng300.platform.store.UserStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

/**
 * Profile dashboard: read-only ELO, rank badges, and leaderboards. Account changes: {@link SettingsController}.
 */
public class ProfileDashboardController {

    @FXML private Label loggedInAsLabel;
    @FXML private Label tttEloLabel;
    @FXML private Label tttGamesLabel;
    @FXML private Label tttRankBadge;
    @FXML private Label c4EloLabel;
    @FXML private Label c4GamesLabel;
    @FXML private Label c4RankBadge;
    @FXML private Label tierSummaryLabel;
    @FXML private ListView<String> leaderboardList;

    @FXML
    private void initialize() {
        refreshLoggedInLine();
        refreshStats();
    }

    private void refreshLoggedInLine() {
        if (loggedInAsLabel == null) {
            return;
        }
        User u = currentUser();
        if (u == null) {
            loggedInAsLabel.setText("Not logged in.");
            return;
        }
        loggedInAsLabel.setText("Signed in as: " + u.getlogin() + "  ·  " + u.getEmail());
    }

    private User currentUser() {
        int id = CentralController.currentUserId;
        if (id == 0) {
            return null;
        }
        return UserStore.getInstance().get(id);
    }

    private void refreshStats() {
        int tttElo = PlayerProfileHelper.eloForGameId(Game.TICK_TACK_TOE);
        int c4Elo = PlayerProfileHelper.eloForGameId(Game.CONNECT_FOUR);
        int tttGames = PlayerProfileHelper.gamesForGameId(Game.TICK_TACK_TOE);
        int c4Games = PlayerProfileHelper.gamesForGameId(Game.CONNECT_FOUR);

        if (tttEloLabel != null) {
            tttEloLabel.setText(String.valueOf(tttElo));
        }
        if (tttGamesLabel != null) {
            tttGamesLabel.setText(tttGames + " games played");
        }
        if (tttRankBadge != null) {
            tttRankBadge.setText(PlayerProfileHelper.eloTierBadge(tttElo));
        }
        if (c4EloLabel != null) {
            c4EloLabel.setText(String.valueOf(c4Elo));
        }
        if (c4GamesLabel != null) {
            c4GamesLabel.setText(c4Games + " games played");
        }
        if (c4RankBadge != null) {
            c4RankBadge.setText(PlayerProfileHelper.eloTierBadge(c4Elo));
        }
        if (tierSummaryLabel != null) {
            tierSummaryLabel.setText(String.format(
                    "Ranks: TTT %s  ·  C4 %s",
                    PlayerProfileHelper.eloTierLabel(tttElo),
                    PlayerProfileHelper.eloTierLabel(c4Elo)));
        }
        if (leaderboardList != null) {
            leaderboardList.getItems().clear();
            leaderboardList.getItems().add("— Tic Tac Toe (by ELO) —");
            appendLeaderboardRows(Game.TICK_TACK_TOE);
            leaderboardList.getItems().add("— Connect 4 (by ELO) —");
            appendLeaderboardRows(Game.CONNECT_FOUR);
        }
    }

    private void appendLeaderboardRows(int gameId) {
        List<User> top = LeaderBoardService.getInstance().getLeaderBoard(true, gameId, 20);
        if (top.isEmpty()) {
            leaderboardList.getItems().add("  (no players yet)");
            return;
        }
        for (int i = 0; i < top.size(); i++) {
            User u = top.get(i);
            int elo = u.getEloForGame(gameId);
            leaderboardList.getItems().add(String.format(
                    "  #%d  %s  —  %d ELO  (%s)",
                    i + 1,
                    u.getlogin(),
                    elo,
                    PlayerProfileHelper.eloTierLabel(elo)));
        }
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
