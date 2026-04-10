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

import java.util.ArrayList;
import java.util.List;

public class ProfileDashboardController {

    @FXML private Label loggedInAsLabel;
    @FXML private Label tttScoreLabel;
    @FXML private Label tttGamesLabel;
    @FXML private Label tttRankLabel;
    @FXML private Label c4ScoreLabel;
    @FXML private Label c4GamesLabel;
    @FXML private Label c4RankLabel;
    @FXML private ListView<String> tttLeaderboardList;
    @FXML private ListView<String> c4LeaderboardList;

    private CentralController cent;

    public void setCentralController(CentralController controller) {
        this.cent = controller;
        refresh();
    }

    @FXML
    public void initialize() {
        refresh();
    }

    private void refresh() {
        Integer uid = currentUserId();
        if (uid == null) {
            if (loggedInAsLabel != null) {
                loggedInAsLabel.setText("Not signed in.");
            }
            return;
        }

        User user = UserStore.getInstance().get(uid);
        if (loggedInAsLabel != null) {
            if (user != null) {
                loggedInAsLabel.setText("Signed in as: " + user.getlogin() + "  ·  " + user.getEmail());
            } else {
                loggedInAsLabel.setText("Signed in user id: " + uid);
            }
        }

        PlayerStats tttStats = findStats(uid, Game.TICK_TACK_TOE);
        PlayerStats c4Stats = findStats(uid, Game.CONNECT_FOUR);

        applyStats(tttStats, tttScoreLabel, tttGamesLabel, tttRankLabel);
        applyStats(c4Stats, c4ScoreLabel, c4GamesLabel, c4RankLabel);
        populateLeaderboard(Game.TICK_TACK_TOE, tttLeaderboardList);
        populateLeaderboard(Game.CONNECT_FOUR, c4LeaderboardList);
    }

    private void applyStats(PlayerStats stats, Label score, Label games, Label rank) {
        if (stats == null) {
            if (score != null) score.setText("0.0");
            if (games != null) games.setText("0 games");
            if (rank != null) rank.setText("Bronze");
            return;
        }
        if (score != null) score.setText(String.format("%.1f", stats.getScore()));
        if (games != null) games.setText(stats.getGameCounter() + " games");
        if (rank != null) {
            Rank r = Helper.getRank(stats.getGameCounter(), stats.getScore());
            rank.setText(r.getDisplayName());
        }
    }

    private void populateLeaderboard(int gameId, ListView<String> target) {
        if (target == null) {
            return;
        }
        List<PlayerStats> top = LeaderBoardService.getInstance().getLeaderBoard(true, gameId, 8);
        List<String> rows = new ArrayList<>();
        for (int i = 0; i < top.size(); i++) {
            PlayerStats ps = top.get(i);
            User u = UserStore.getInstance().get(ps.getUserID());
            String name = u != null ? u.getlogin() : ("User " + ps.getUserID());
            Rank rank = Helper.getRank(ps.getGameCounter(), ps.getScore());
            rows.add("#" + (i + 1) + "  " + name + "  |  " + rank.getDisplayName() + "  |  Score " + String.format("%.1f", ps.getScore()));
        }
        target.setItems(FXCollections.observableArrayList(rows));
    }

    private PlayerStats findStats(int userId, int gameId) {
        List<PlayerStats> all = ca.ucalgary.seng300.platform.store.PlayerStatsStore.getInstance().findPlayersByGameID(gameId);
        for (PlayerStats ps : all) {
            if (ps.getUserID() == userId) {
                return ps;
            }
        }
        return null;
    }

    private Integer currentUserId() {
        if (cent != null && cent.clientCore != null && cent.clientCore.CID != null) {
            try {
                return Integer.parseInt(cent.clientCore.CID.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        if (cent != null && cent.clientCore != null && cent.clientCore.username != null) {
            List<User> users = UserStore.getInstance().findByUserName(cent.clientCore.username);
            if (!users.isEmpty()) {
                return users.getFirst().getID();
            }
        }
        return null;
    }

    @FXML
    void handleBack(MouseEvent event) {
        if (CentralController.previousScreen != null && !CentralController.previousScreen.isBlank()) {
            CentralController.moveTo(CentralController.previousScreen);
            return;
        }
        CentralController.moveTo("GameDashboardNew.fxml");
    }
}
