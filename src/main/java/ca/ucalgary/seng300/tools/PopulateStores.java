package ca.ucalgary.seng300.tools;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.PlayerStatsStore;
import ca.ucalgary.seng300.platform.store.SessionHistoryStore;
import ca.ucalgary.seng300.platform.store.UserStore;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility script to populate file-backed stores with sample data.
 *
 * Usage:
 *   - Run with no args to append sample data to existing files.
 *   - Run with "--reset" to wipe target data files first, then insert sample data.
 */
public class PopulateStores {

    private record NewUser(String login, String email, String passwordHash) {}

    public static void main(String[] args) {
        boolean reset = args != null && args.length > 0 && "--reset".equalsIgnoreCase(args[0]);
        if (reset) {
            safeResetDataFiles();
        }

        UserStore userStore = UserStore.getInstance();
        SessionHistoryStore sessionStore = SessionHistoryStore.getInstance();
        PlayerStatsStore statsStore = PlayerStatsStore.getInstance();

        List<Integer> createdUserIds = new ArrayList<>();
        List<UUID> createdSessionUuids = new ArrayList<>();

        // 1) Create Users
        List<NewUser> usersToCreate = List.of(
                new NewUser("alice", "alice@example.com", "hash_alice"),
                new NewUser("bob", "bob@example.com", "hash_bob"),
                new NewUser("carol", "carol@example.com", "hash_carol")
        );

        for (NewUser u : usersToCreate) {
            int id = userStore.assignNewId();
            userStore.create(new User(id, u.login(), u.email(), u.passwordHash()));
            createdUserIds.add(id);
        }

        // 2) Create a session per user
        for (int userId : createdUserIds) {
            int sessId = sessionStore.assignNewId();
            UUID uuid = UUID.randomUUID();
            sessionStore.create(new SessionHistory(sessId, userId, uuid));
            createdSessionUuids.add(uuid);
        }

        // 3) Create PlayerStats (for both games) and update some for variety
        for (int userId : createdUserIds) {
            // TicTacToe stats
            int psId1 = statsStore.assignNewId();
            PlayerStats ttt = new PlayerStats(psId1, Game.TICK_TACK_TOE, userId);
            // Give each user some results
            ttt.updateStatsWin(10); // 1 game, +10
            statsStore.create(ttt);

            // Connect Four stats
            int psId2 = statsStore.assignNewId();
            PlayerStats c4 = new PlayerStats(psId2, Game.CONNECT_FOUR, userId);
            // Mix up wins/loses
            if (userId % 2 == 0) {
                c4.updateStatsWin(15); // 1 game, +15
            } else {
                c4.updateStatsLose(5); // 1 game, -5 (floored at 0)
            }
            statsStore.create(c4);
        }

        // Summary
        System.out.println("PopulateStores finished.");
        System.out.println("  Users created:  " + createdUserIds.size() + " -> " + createdUserIds);
        System.out.println("  Sessions made:  " + createdSessionUuids.size());
        System.out.println("  PlayerStats:    " + createdUserIds.size() * 2 + " (2 games each)");
        System.out.println("Data files:");
        System.out.println("  - " + Constants.USER_FILE_PATH);
        System.out.println("  - " + Constants.SESSION_FILE_PATH);
        System.out.println("  - " + Constants.PLAYER_STATS_FILE_PATH);
    }

    private static void safeResetDataFiles() {
        // Overwrite with an empty JSON object so the Store loader can read it cleanly
        writeEmptyJson(Constants.USER_FILE_PATH);
        writeEmptyJson(Constants.SESSION_FILE_PATH);
        writeEmptyJson(Constants.PLAYER_STATS_FILE_PATH);
        System.out.println("Data files reset to empty objects.");
    }

    private static void writeEmptyJson(String relativePath) {
        try {
            Path p = Path.of(relativePath);
            if (p.getParent() != null) {
                Files.createDirectories(p.getParent());
            }
            Files.writeString(p, "{}", StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Failed to reset file: " + relativePath + " -> " + e.getMessage());
        }
    }
}
