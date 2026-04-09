package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.services.LeaderBoardService;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.utils.Helper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class LeaderboardController {
    private static final int DEFAULT_COUNT = 25;

    @FXML
    private Label gameTitleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private ListView<String> leaderboardList;

    @FXML
    public void initialize() {
        int gameId = resolveGameId();
        String gameName = gameId == Game.CONNECT_FOUR ? "Connect 4" : "Tic-Tac-Toe";
        if (gameTitleLabel != null) {
            gameTitleLabel.setText(gameName + " Leaderboard");
        }
        if (subtitleLabel != null) {
            subtitleLabel.setText("Showing top players by rank, score, and games played");
        }
        if (leaderboardList != null) {
            leaderboardList.setPlaceholder(new Label("No leaderboard entries available."));
            leaderboardList.setItems(FXCollections.observableArrayList(buildLeaderboardRows(gameId)));
        }
    }

    @FXML
    void handleBack(MouseEvent event) {
        CentralController.moveTo("chooseModeNew.fxml");
    }

    private List<String> buildLeaderboardRows(int gameId) {
        List<PlayerStats> leaderboard = LeaderBoardService.getInstance().getLeaderBoard(true, gameId, DEFAULT_COUNT);
        return leaderboard.stream()
                .map(this::formatRow)
                .toList();
    }

    private String formatRow(PlayerStats stats) {
        User user = UserStore.getInstance().get(stats.getUserID());
        String username = user != null && user.getlogin() != null && !user.getlogin().isBlank()
                ? user.getlogin()
                : "Player";
        Rank rank = Helper.getRank(stats.getGameCounter(), stats.getScore());
        return username
                + " | Rank: " + rank.getDisplayName()
                + " | Score: " + formatScore(stats.getScore())
                + " | Games: " + stats.getGameCounter();
    }

    private int resolveGameId() {
        if (CentralController.selectedGameId > 0) {
            return CentralController.selectedGameId;
        }
        String selectedGame = CentralController.selectedGame != null ? CentralController.selectedGame : "";
        return selectedGame.toLowerCase().contains("connect") ? Game.CONNECT_FOUR : Game.TICK_TACK_TOE;
    }

    private String formatScore(double score) {
        if (score == Math.rint(score)) {
            return String.valueOf((int) score);
        }
        return String.format("%.1f", score);
    }
}
