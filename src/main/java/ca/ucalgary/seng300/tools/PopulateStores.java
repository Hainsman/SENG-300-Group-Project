package ca.ucalgary.seng300.tools;

import ca.ucalgary.seng300.platform.server.PlatformCore;
import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.domain.FriendHistory;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.domain.Participant;
import ca.ucalgary.seng300.platform.store.FriendHistoryStore;
import ca.ucalgary.seng300.platform.store.PlayerStatsStore;
import ca.ucalgary.seng300.platform.store.SessionHistoryStore;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.store.GameHistoryStore;
import ca.ucalgary.seng300.platform.store.ParticipantStore;
import ca.ucalgary.seng300.platform.utils.Helper;

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
        // Prevent PlatformCore from starting a real server if it's initialized
        PlatformCore.main(new String[]{"NO_SERVER"});

        boolean reset = args != null && args.length > 0 && "--reset".equalsIgnoreCase(args[0]);
        if (reset) {
            safeResetDataFiles();
        }

        UserStore userStore = UserStore.getInstance();
        SessionHistoryStore sessionStore = SessionHistoryStore.getInstance();
        PlayerStatsStore statsStore = PlayerStatsStore.getInstance();
        FriendHistoryStore friendStore = FriendHistoryStore.getInstance();
        GameHistoryStore gameHistoryStore = GameHistoryStore.getInstance();
        ParticipantStore participantStore = ParticipantStore.getInstance();

        List<Integer> createdUserIds = new ArrayList<>();
        List<UUID> createdSessionUuids = new ArrayList<>();

        // 1) Create Users
        List<NewUser> usersToCreate = List.of(
                new NewUser("Daniel M", "danielM@ucalgary.ca", Helper.hashPassword("hash1", Constants.HASH_ALGORITHM)),
                new NewUser("Alex", "alex.smith@ucalgary.ca", Helper.hashPassword("hash2", Constants.HASH_ALGORITHM)),
                new NewUser("John W", "JohnW@ucalgary.ca", Helper.hashPassword("hash3", Constants.HASH_ALGORITHM)),
                new NewUser("Paul", "Paul.C@ucalgary.ca", Helper.hashPassword("hash4", Constants.HASH_ALGORITHM))
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

        // 4) Create Friends
        // Daniel M (index 0) is friends with Alex (index 1) and Paul (index 3)
        // John W (index 2) is friends with Alex (index 1)
        if (createdUserIds.size() >= 4) {
            int danielId = createdUserIds.get(0);
            int alexId = createdUserIds.get(1);
            int johnId = createdUserIds.get(2);
            int paulId = createdUserIds.get(3);

            // Daniel M <-> Alex
            addFriend(friendStore, danielId, alexId);
            // Daniel M <-> Paul
            addFriend(friendStore, danielId, paulId);
            // John W <-> Alex
            addFriend(friendStore, johnId, alexId);
        }

        // 5) Create Game Histories and Participants
        List<Integer> createdGameHistoryIds = new ArrayList<>();

        // Game 1: TicTacToe between first two users
        int ghId1 = gameHistoryStore.assignNewId();
        GameHistory gh1 = new GameHistory(ghId1, Game.TICK_TACK_TOE);
        gameHistoryStore.create(gh1);
        createdGameHistoryIds.add(ghId1);

        // Participants for game 1
        int pId1 = participantStore.assignNewId();
        Participant p1 = new Participant(pId1, ghId1, createdUserIds.get(0), 1); // user 1 wins
        participantStore.create(p1);

        int pId2 = participantStore.assignNewId();
        Participant p2 = new Participant(pId2, ghId1, createdUserIds.get(1), -1); // user 2 loses
        participantStore.create(p2);

        // Game 2: Connect4 between second and third users
        int ghId2 = gameHistoryStore.assignNewId();
        GameHistory gh2 = new GameHistory(ghId2, Game.CONNECT_FOUR);
        gameHistoryStore.create(gh2);
        createdGameHistoryIds.add(ghId2);

        // Participants for game 2
        int pId3 = participantStore.assignNewId();
        Participant p3 = new Participant(pId3, ghId2, createdUserIds.get(1), 1); // user 2 wins
        participantStore.create(p3);

        int pId4 = participantStore.assignNewId();
        Participant p4 = new Participant(pId4, ghId2, createdUserIds.get(2), -1); // user 3 loses
        participantStore.create(p4);

        // Game 3: Another TicTacToe between third and fourth users
        int ghId3 = gameHistoryStore.assignNewId();
        GameHistory gh3 = new GameHistory(ghId3, Game.TICK_TACK_TOE);
        gameHistoryStore.create(gh3);
        createdGameHistoryIds.add(ghId3);

        // Participants for game 3
        int pId5 = participantStore.assignNewId();
        Participant p5 = new Participant(pId5, ghId3, createdUserIds.get(2), 0); // user 3 ties
        participantStore.create(p5);

        int pId6 = participantStore.assignNewId();
        Participant p6 = new Participant(pId6, ghId3, createdUserIds.get(3), 0); // user 4 ties
        participantStore.create(p6);

        // Summary
        System.out.println("PopulateStores finished.");
        System.out.println("  Users created:  " + createdUserIds.size() + " -> " + createdUserIds);
        System.out.println("  Sessions made:  " + createdSessionUuids.size());
        System.out.println("  PlayerStats:    " + createdUserIds.size() * 2 + " (2 games each)");
        System.out.println("  Game Histories: " + createdGameHistoryIds.size());
        System.out.println("  Participants:   " + createdGameHistoryIds.size() * 2 + " (2 per game)");
        System.out.println("Data files:");
        System.out.println("  - " + Constants.USER_FILE_PATH);
        System.out.println("  - " + Constants.SESSION_FILE_PATH);
        System.out.println("  - " + Constants.PLAYER_STATS_FILE_PATH);
        System.out.println("  - " + Constants.FRIENDS_FILE_PATH);
        System.out.println("  - " + Constants.GAME_FILE_PATH);
        System.out.println("  - " + Constants.PARTICIPANTS_FILE_PATH);
    }

    private static void addFriend(FriendHistoryStore store, int userId, int friendId) {
        int id = store.assignNewId();
        FriendHistory fh = new FriendHistory(id);
        fh.setUserId(userId);
        fh.setFriendUserId(friendId);
        fh.setFriendShipActive(true);
        store.create(fh);

        int id2 = store.assignNewId();
        FriendHistory fh2 = new FriendHistory(id2);
        fh2.setUserId(friendId);
        fh2.setFriendUserId(userId);
        fh2.setFriendShipActive(true);
        store.create(fh2);
    }

    private static void safeResetDataFiles() {
        // Overwrite with an empty JSON object so the Store loader can read it cleanly
        writeEmptyJson(Constants.USER_FILE_PATH);
        writeEmptyJson(Constants.SESSION_FILE_PATH);
        writeEmptyJson(Constants.PLAYER_STATS_FILE_PATH);
        writeEmptyJson(Constants.FRIENDS_FILE_PATH);
        writeEmptyJson(Constants.GAME_FILE_PATH);
        writeEmptyJson(Constants.PARTICIPANTS_FILE_PATH);
        System.out.println("Data files reset to empty objects.");
    }

    private static void writeEmptyJson(String relativePath) {
        try {
            Path p = Path.of(relativePath).toAbsolutePath();
            if (p.getParent() != null) {
                Files.createDirectories(p.getParent());
            }
            Files.writeString(p, "{}", StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Failed to reset file: " + relativePath + " -> " + e.getMessage());
        }
    }
}