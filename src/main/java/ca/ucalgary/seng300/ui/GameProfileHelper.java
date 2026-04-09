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
    private GameProfileHelper() {}

    public static ProfileData loadProfile(int gameId) {
        String username = "Player";
        String email = "player@example.com";
        double score = 0.0;
        int gamesPlayed = 0;

        CentralController controller = CentralController.getInstance();
        ClientCore client = controller != null ? controller.getCC() : null;
        if (client != null && client.username != null && !client.username.isBlank()) {
            username = client.username;
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
