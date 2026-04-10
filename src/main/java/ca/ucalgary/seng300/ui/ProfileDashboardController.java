package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.domain.Participant;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.services.LeaderBoardService;
import ca.ucalgary.seng300.platform.store.GameHistoryStore;
import ca.ucalgary.seng300.platform.store.ParticipantStore;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.utils.Helper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileDashboardController {

    @FXML private Label loggedInAsLabel;
    @FXML private Label tttScoreLabel;
    @FXML private Label tttGamesLabel;
    @FXML private Label tttRankLabel;
    @FXML private Label c4ScoreLabel;
    @FXML private Label c4GamesLabel;
    @FXML private Label c4RankLabel;
    @FXML private ListView<String> tttHistoryList;
    @FXML private ListView<String> c4HistoryList;

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

        requestPlayerStats(uid);
        requestGameHistory();
    }

    private void requestPlayerStats(int uid) {
        if (cent == null || cent.clientCore == null) return;
        
        cent.clientCore.setPlayerStatsHandler(this::handlePlayerStatsResponse);
        
        // Request stats for Tic-Tac-Toe (Game ID 1)
        cent.clientCore.msgSend(new ca.ucalgary.seng300.platform.messages.sysMSG("PLAYER_STATS_REQUEST", String.valueOf(uid), String.valueOf(Game.TICK_TACK_TOE)));
        // Request stats for Connect 4 (Game ID 2)
        cent.clientCore.msgSend(new ca.ucalgary.seng300.platform.messages.sysMSG("PLAYER_STATS_REQUEST", String.valueOf(uid), String.valueOf(Game.CONNECT_FOUR)));
    }

    private void handlePlayerStatsResponse(ca.ucalgary.seng300.platform.messages.sysMSG response) {
        // PLAYER_STATS: fieldA = score, fieldB = gameCounter, payload = rank + "|" + gameId
        String payload = response.getPayload();
        if (payload == null || !payload.contains("|")) return;
        
        String[] parts = payload.split("\\|");
        String rankStr = parts[0];
        int gameId = Integer.parseInt(parts[1]);
        
        String scoreStr = response.getFieldA();
        String gamesStr = response.getFieldB();

        javafx.application.Platform.runLater(() -> {
            if (gameId == Game.TICK_TACK_TOE) {
                if (tttScoreLabel != null) tttScoreLabel.setText(scoreStr);
                if (tttGamesLabel != null) tttGamesLabel.setText(gamesStr + " games");
                if (tttRankLabel != null) tttRankLabel.setText(rankStr);
            } else if (gameId == Game.CONNECT_FOUR) {
                if (c4ScoreLabel != null) c4ScoreLabel.setText(scoreStr);
                if (c4GamesLabel != null) c4GamesLabel.setText(gamesStr + " games");
                if (c4RankLabel != null) c4RankLabel.setText(rankStr);
            }
        });
    }

    private void requestGameHistory() {
        if (cent == null || cent.clientCore == null) return;
        
        cent.clientCore.setGameHistoryHandler(this::handleGameHistoryResponse);
        cent.clientCore.msgSend(new ca.ucalgary.seng300.platform.messages.sysMSG("GAME_HISTORY_REQUEST", ""));
    }

    private void handleGameHistoryResponse(ca.ucalgary.seng300.platform.messages.sysMSG response) {
        if (!"PASS".equals(response.getFieldA())) return;
        
        String payload = response.getPayload();
        List<String> tttRows = new ArrayList<>();
        List<String> c4Rows = new ArrayList<>();
        
        if (payload != null && !payload.isBlank()) {
            String[] entries = payload.split(";");
            // Reverse order to show latest first
            for (int i = entries.length - 1; i >= 0; i--) {
                String entry = entries[i];
                String[] parts = entry.split("\\|");
                if (parts.length < 3) continue;
                
                int gameId = Integer.parseInt(parts[0]);
                double scoreValue = Double.parseDouble(parts[1]);
                long timestamp = Long.parseLong(parts[2]);
                
                String result = (scoreValue > 0) ? "WIN" : (scoreValue < 0 ? "LOSS" : "DRAW");
                String timeStr = ca.ucalgary.seng300.platform.domain.Record.getTimeCreatedReadableFormat(timestamp);
                
                String row = "Result: " + result + "  |  Score: " + formatScore(scoreValue) + "  |  Time: " + timeStr;
                
                if (gameId == Game.TICK_TACK_TOE) {
                    tttRows.add(row);
                } else if (gameId == Game.CONNECT_FOUR) {
                    c4Rows.add(row);
                }
            }
        }
        
        if (tttRows.isEmpty()) tttRows.add("No games played yet.");
        if (c4Rows.isEmpty()) c4Rows.add("No games played yet.");
        
        javafx.application.Platform.runLater(() -> {
            if (tttHistoryList != null) tttHistoryList.setItems(FXCollections.observableArrayList(tttRows));
            if (c4HistoryList != null) c4HistoryList.setItems(FXCollections.observableArrayList(c4Rows));
        });
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

    private String formatScore(double score) {
        if (score == Math.rint(score)) {
            return String.valueOf((int) score);
        }
        return String.format("%.1f", score);
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
