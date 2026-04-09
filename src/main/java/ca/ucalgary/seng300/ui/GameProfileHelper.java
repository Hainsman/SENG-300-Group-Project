package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.PlayerStatsStore;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.utils.Helper;

import java.util.List;

public final class GameProfileHelper {
    public enum LocalMatchResult {
        WIN,
        LOSS,
        DRAW,
        FORFEIT
    }

    private GameProfileHelper() {}

    public static ProfileData loadProfile(int gameId) {
        String username = "Player";
        String email = "player@example.com";
        double score = 0.0;
        int gamesPlayed = 0;

        String currentUsername = resolveCurrentUsername();
        if (currentUsername != null && !currentUsername.isBlank()) {
            username = currentUsername;
        }

        List<User> matchingUsers = UserStore.getInstance().findByUserName(username);
        if (!matchingUsers.isEmpty()) {
            User user = matchingUsers.getFirst();
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                email = user.getEmail();
            }
            List<PlayerStats> stats = PlayerStatsStore.getInstance().findPlayersByGameID(gameId);
            for (PlayerStats playerStats : stats) {
                if (playerStats.getUserID() == user.getID()) {
                    score = playerStats.getScore();
                    gamesPlayed = playerStats.getGameCounter();
                    break;
                }
            }
        }

        Rank rank = Helper.getRank(gamesPlayed, score);
        return new ProfileData(
                username,
                email,
                formatScore(score),
                rank.getDisplayName(),
                String.valueOf(gamesPlayed),
                gameId == Game.CONNECT_FOUR ? "Connect 4" : "Tic-Tac-Toe"
        );
    }

    public static void applyLocalResult(int gameId, LocalMatchResult result) {
        Integer userId = resolveCurrentUserId();
        if (userId == null) {
            return;
        }

        PlayerStatsStore statsStore = PlayerStatsStore.getInstance();
        PlayerStats stats = getOrCreatePlayerStats(statsStore, gameId, userId);
        switch (result) {
            case WIN -> stats.updateStatsWin(1.0);
            case LOSS -> stats.updateStatsLose(1.0);
            case DRAW, FORFEIT -> stats.updateStatsWin(0.0);
        }
        statsStore.update(stats);
    }

    private static PlayerStats getOrCreatePlayerStats(PlayerStatsStore statsStore, int gameId, int userId) {
        List<PlayerStats> stats = statsStore.findPlayersByGameID(gameId);
        for (PlayerStats playerStats : stats) {
            if (playerStats.getUserID() == userId) {
                return playerStats;
            }
        }
        PlayerStats created = new PlayerStats(statsStore.assignNewId(), gameId, userId);
        statsStore.create(created);
        return created;
    }

    private static Integer resolveCurrentUserId() {
        String username = resolveCurrentUsername();
        if (username == null || username.isBlank()) {
            return null;
        }
        List<User> matchingUsers = UserStore.getInstance().findByUserName(username);
        if (matchingUsers.isEmpty()) {
            return null;
        }
        return matchingUsers.getFirst().getID();
    }

    private static String resolveCurrentUsername() {
        CentralController controller = CentralController.getInstance();
        ClientCore client = controller != null ? controller.getCC() : null;
        if (client != null && client.username != null && !client.username.isBlank()) {
            return client.username;
        }
        return null;
    }

    private static String formatScore(double score) {
        if (score == Math.rint(score)) {
            return String.valueOf((int) score);
        }
        return String.format("%.1f", score);
    }

    public record ProfileData(
            String username,
            String email,
            String elo,
            String tier,
            String gamesPlayed,
            String gameTitle
    ) {}
}
