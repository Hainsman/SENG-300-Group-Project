package ca.ucalgary.seng300.tests;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.*;
import ca.ucalgary.seng300.platform.domain.Record;
import ca.ucalgary.seng300.platform.services.MatchMakingService;
import ca.ucalgary.seng300.platform.store.*;
import ca.ucalgary.seng300.platform.utils.Helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Manual integration runner. Uses a temp directory for all {@link Store} files so it never overwrites project {@code datafiles/}.
 */
public class MainAppTest {
    public static void main(String[] args) throws IOException {
//        launch(args);
        Path tempDir = Files.createTempDirectory("seng300-mainapptest");
        tempDir.toFile().deleteOnExit();

        UserStore userStorage = new UserStore(tempDir.resolve("user.txt").toString());
        userStorage.create(new User(1, "Daniel", "daniel.maximov@ucalgary.ca", Helper.hashPassword("ABC", Constants.HASH_ALGORITHM)));
        userStorage.create(new User(2, "Alex", "alex.smith@ucalgary.ca", Helper.hashPassword("DEF", Constants.HASH_ALGORITHM)));
        userStorage.create(new User(3, "John", "John.Johnson@ucalgary,ca", Helper.hashPassword("ABC", Constants.HASH_ALGORITHM)));
        User paul =  new User(4, "Paul", "Paul.C@ucalgary.ca", Helper.hashPassword("jdshakjdbskj", Constants.HASH_ALGORITHM));
        userStorage.create(paul);
//        userStorage.delete(paul);
        System.out.println(userStorage.findByUserName("Alex").getFirst().getEmail());

//        //show that email addresses are true and legit:
//        System.out.println(Helper.isEmailValid("daniel.maximov@ucalgary.ca", Constants.EMAIL_REGEX));
//        System.out.println(Helper.isEmailValid("\"john.doe\"@example.com", Constants.EMAIL_REGEX));
//        System.out.println(Helper.isEmailValid("user=name@example.com", Constants.EMAIL_REGEX));

        for (int i = 1; i < 4; i++){
            System.out.println(Record.getTimeCreatedReadableFormat(userStorage.get(i).getTimeCreated()));
        }


        SessionHistoryStore sessionHistoryStore = new SessionHistoryStore(tempDir.resolve("sessionHistory.txt").toString());
        sessionHistoryStore.create(new SessionHistory(1, 1, UUID.randomUUID()));
        sessionHistoryStore.create(new SessionHistory(2, 2, UUID.randomUUID()));
        sessionHistoryStore.create(new SessionHistory(3, 3, UUID.randomUUID()));
        sessionHistoryStore.create(new SessionHistory(4, 4, UUID.randomUUID()));

//        GameHistoryStore gameHistoryStore = new GameHistoryStore(Constants.GAME_FILE_PATH);
        GameHistoryStore gameHistoryStore = new GameHistoryStore(tempDir.resolve("gameHistory.txt").toString());
        gameHistoryStore.create(new GameHistory(1, Game.TICK_TACK_TOE));
        gameHistoryStore.create(new GameHistory(2, Game.TICK_TACK_TOE));
        gameHistoryStore.create(new GameHistory(3, Game.TICK_TACK_TOE));
        GameHistory gameHistory1 = new GameHistory(4, Game.TICK_TACK_TOE);
        gameHistoryStore.create(new GameHistory(5, Game.TICK_TACK_TOE));
        gameHistoryStore.create(new GameHistory(6, Game.TICK_TACK_TOE));
        gameHistoryStore.create(new GameHistory(7, Game.TICK_TACK_TOE));
        gameHistoryStore.create(new GameHistory(8, Game.TICK_TACK_TOE));
        gameHistoryStore.create(new GameHistory(9, Game.TICK_TACK_TOE));
        gameHistoryStore.create(new GameHistory(10, Game.TICK_TACK_TOE));
        gameHistoryStore.create(gameHistory1);
//
        ParticipantStore participantStore = new ParticipantStore(tempDir.resolve("participants.txt").toString());
        participantStore.create(new Participant(1, 1, 1, 1));
        participantStore.create(new Participant(2, 2, 1, 1));
        participantStore.create(new Participant(3, 3, 1, 1));
        participantStore.create(new Participant(4, 4, 1, 1));
        participantStore.create(new Participant(5, 5, 1, 1));
        participantStore.create(new Participant(6, 6, 1, 1));
        participantStore.create(new Participant(7, 7, 1, 1));
        participantStore.create(new Participant(8, 8, 1, 1));
        participantStore.create(new Participant(9, 9, 1, 1));
        participantStore.create(new Participant(10, 10, 1, 80));

        PlayerStatsStore playerStatsStore = new PlayerStatsStore(tempDir.resolve("playerStats.txt").toString());
        PlayerStats playerOne = new PlayerStats(1, Game.TICK_TACK_TOE, 1);
        PlayerStats playerTwo = new PlayerStats(2, Game.TICK_TACK_TOE, 2);
        PlayerStats playerThree = new PlayerStats(3, Game.TICK_TACK_TOE, 3);
        PlayerStats playerFour = new PlayerStats(4, Game.TICK_TACK_TOE, 4);

        for (int i = 0; i < 10; i++){
            playerOne.updateStats(5.0);
        }

        for (int i = 0; i < 10; i++){
            playerTwo.updateStats(3.0);
        }

        for (int i = 0; i < 20; i++){
            playerThree.updateStats(1.5);
        }

        for (int i = 0; i < 10; i++){
            playerFour.updateStats(1.0);
        }


        playerStatsStore.create(playerOne);
        playerStatsStore.create(playerTwo);
        playerStatsStore.create(playerThree);
        playerStatsStore.create(playerFour);
        playerStatsStore.create(playerOne);
//

        List<PlayerStats> listOfPlayers = playerStatsStore.findPlayersByGameID(Game.TICK_TACK_TOE);
        List<User> listOfUsers = userStorage.findByUserEmail("daniel.maximov@ucalgary.ca");
        List<GameHistory> listOfGames = gameHistoryStore.findByGameID(Game.TICK_TACK_TOE);
        UUID uniqueIdent = UUID.fromString("be6b47b1-2cb6-414d-a7df-945abde64c5e");
        List<SessionHistory> listOfSessions = sessionHistoryStore.findSessionByUUID(uniqueIdent);
        List<Participant> listOfParticipants = participantStore.findUserParticipationInGameEvents(1);
//
        Rank rank = Helper.getRank(playerFour.getGameCounter(), playerFour.getGameCounter());
        System.out.println(rank);
        System.out.println(listOfPlayers.size());
        System.out.println(listOfUsers.getFirst().getlogin());
        System.out.println(listOfGames.size());
//        System.out.println(listOfSessions.getFirst().getGloballyUniqueSessionIdentifier());
        List<User> sortedUsers = buildSortedUserLeaderBoardSlice(userStorage, Game.TICK_TACK_TOE, 4);

        for (int i = 0; i < sortedUsers.size(); i++) {
            User row = sortedUsers.get(i);
            System.out.println(row.getID());
            System.out.println(row.getlogin() + " TTT ELO=" + row.getEloForGame(1));
        }

// ============================================================
// MatchMakingService Tests
// ============================================================
        MatchMakingService matchMaking = new MatchMakingService();

// setting up players with known scores for TicTacToe
// mmPlayer1: score = 50.0  (10 x 5.0)
// mmPlayer2: score = 53.0  (10 x 5.3)  <- diff from player1 = 3.0
// mmPlayer3: score = 90.0  (10 x 9.0)  <- too far from everyone
// mmPlayer4: score = 35.0  (10 x 3.5)
// mmPlayer5: score = 35.1  (10 x 3.51) <- closest pair overall, diff = 0.1
// mmPlayer6: score = 34.0  (10 x 3.4)  <- diff from player5 = 1.1, diff from player4 = 1.0
        PlayerStats mmPlayer1 = new PlayerStats(101, Game.TICK_TACK_TOE, 101);
        PlayerStats mmPlayer2 = new PlayerStats(102, Game.TICK_TACK_TOE, 102);
        PlayerStats mmPlayer3 = new PlayerStats(103, Game.TICK_TACK_TOE, 103);
        PlayerStats mmPlayer4 = new PlayerStats(104, Game.TICK_TACK_TOE, 104);
        PlayerStats mmPlayer5 = new PlayerStats(105, Game.TICK_TACK_TOE, 105);
        PlayerStats mmPlayer6 = new PlayerStats(106, Game.TICK_TACK_TOE, 106);
        PlayerStats mmPlayer7 = new PlayerStats(107, Game.CONNECT_FOUR, 107);
        PlayerStats mmPlayer8 = new PlayerStats(108, Game.CONNECT_FOUR, 108);

        for (int i = 0; i < 10; i++) mmPlayer1.updateStats(5.0);   // score = 50.0
        for (int i = 0; i < 10; i++) mmPlayer2.updateStats(5.3);   // score = 53.0
        for (int i = 0; i < 10; i++) mmPlayer3.updateStats(9.0);   // score = 90.0
        for (int i = 0; i < 10; i++) mmPlayer4.updateStats(3.5);   // score = 35.0
        for (int i = 0; i < 10; i++) mmPlayer5.updateStats(3.51);  // score = 35.1
        for (int i = 0; i < 10; i++) mmPlayer6.updateStats(3.4);   // score = 34.0
        for (int i = 0; i < 10; i++) mmPlayer7.updateStats(5.0);   // score = 50.0
        for (int i = 0; i < 10; i++) mmPlayer8.updateStats(5.5);   // score = 55.0

// --- Test 1: addPlayerToQueue adds players correctly ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue(mmPlayer1);
        matchMaking.addPlayerToQueue(mmPlayer2);
        matchMaking.addPlayerToQueue(mmPlayer3);
        System.out.println("Test 1 - Queue size should be 3: " + matchMaking.getQueueSize(Game.TICK_TACK_TOE));

// --- Test 2: removePlayer removes the correct player ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue(mmPlayer1);
        matchMaking.addPlayerToQueue(mmPlayer2);
        MatchMakingService.removePlayer(mmPlayer2);
        System.out.println("Test 2 - Queue size after remove should be 1: " + matchMaking.getQueueSize(Game.TICK_TACK_TOE));

// --- Test 3: matchPlayers returns the closest pair (not just first pair found) ---
// queue has 35.0, 35.1, 34.0 — closest pair is 35.0 and 35.1 (diff=0.1)
// NOT 35.0 and 34.0 (diff=1.0) or 35.1 and 34.0 (diff=1.1)
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue(mmPlayer4); // 35.0
        matchMaking.addPlayerToQueue(mmPlayer5); // 35.1
        matchMaking.addPlayerToQueue(mmPlayer6); // 34.0
        List<PlayerStats> closestMatch = matchMaking.matchPlayers(Game.TICK_TACK_TOE);
        System.out.println("Test 3 - Closest pair should be players 104 and 105 (diff=0.1): " + closestMatch.get(0).getUserID() + " and " + closestMatch.get(1).getUserID());

// --- Test 4: after match, only the unmatched player remains in queue ---
        System.out.println("Test 4 - Queue size should be 1 (player 106 remains): " + matchMaking.getQueueSize(Game.TICK_TACK_TOE));

// --- Test 5: matched players are removed from the queue ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue(mmPlayer1); // 50.0
        matchMaking.addPlayerToQueue(mmPlayer2); // 53.0
        List<PlayerStats> matched = matchMaking.matchPlayers(Game.TICK_TACK_TOE);
        System.out.println("Test 5 - Match should contain players 101 and 102: " + matched.get(0).getUserID() + " and " + matched.get(1).getUserID());
        System.out.println("Test 5 - Queue should be empty after match: " + matchMaking.getQueueSize(Game.TICK_TACK_TOE));

// --- Test 6: no match when score difference exceeds 10 ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue(mmPlayer1); // 50.0
        matchMaking.addPlayerToQueue(mmPlayer3); // 90.0, diff = 40.0
        List<PlayerStats> noMatch = matchMaking.matchPlayers(Game.TICK_TACK_TOE);
        System.out.println("Test 6 - No match when scores too far apart, should be null: " + noMatch);

// --- Test 7: matchPlayers on empty queue returns null ---
        MatchMakingService.queues.clear();
        List<PlayerStats> emptyMatch = matchMaking.matchPlayers(Game.TICK_TACK_TOE);
        System.out.println("Test 7 - Match on empty queue should be null: " + emptyMatch);

// --- Test 8: single player in queue returns null ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue(mmPlayer1);
        List<PlayerStats> singlePlayer = matchMaking.matchPlayers(Game.TICK_TACK_TOE);
        System.out.println("Test 8 - Single player in queue should return null: " + singlePlayer);

// --- Test 9: different game type queues don't interfere ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue(mmPlayer1); // TicTacToe
        matchMaking.addPlayerToQueue(mmPlayer7); // ConnectFour
        matchMaking.addPlayerToQueue(mmPlayer8); // ConnectFour
        System.out.println("Test 9 - TicTacToe queue size should be 1: " + matchMaking.getQueueSize(Game.TICK_TACK_TOE));
        System.out.println("Test 9 - ConnectFour queue size should be 2: " + matchMaking.getQueueSize(Game.CONNECT_FOUR));

// --- Test 10: removePlayer on unknown player does nothing ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue(mmPlayer1);
        PlayerStats unknownPlayer = new PlayerStats(999, Game.TICK_TACK_TOE, 999);
        MatchMakingService.removePlayer(unknownPlayer);
        System.out.println("Test 10 - Queue size should still be 1 after removing unknown player: " + matchMaking.getQueueSize(Game.TICK_TACK_TOE));

// --- Test 11: matching in ConnectFour queue works independently ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue(mmPlayer7); // ConnectFour score 50.0
        matchMaking.addPlayerToQueue(mmPlayer8); // ConnectFour score 55.0, diff = 5.0
        List<PlayerStats> connectFourMatch = matchMaking.matchPlayers(Game.CONNECT_FOUR);
        System.out.println("Test 11 - ConnectFour match should contain players 107 and 108: " + connectFourMatch.get(0).getUserID() + " and " + connectFourMatch.get(1).getUserID());

    }

    /** Same ordering as {@link ca.ucalgary.seng300.platform.services.LeaderBoardService} for tests using a non-singleton {@link UserStore}. */
    private static List<User> buildSortedUserLeaderBoardSlice(UserStore store, int gameId, int count) {
        List<User> users = new ArrayList<>(store.findAll());
        users.sort(Comparator
                .comparingInt((User u) -> u.getEloForGame(gameId)).reversed()
                .thenComparing(User::getlogin, String.CASE_INSENSITIVE_ORDER));
        if (users.isEmpty()) {
            return new ArrayList<>();
        }
        if (count > users.size()) {
            count = users.size();
        }
        return new ArrayList<>(users.subList(0, count));
    }
}
