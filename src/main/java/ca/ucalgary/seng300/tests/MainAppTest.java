package ca.ucalgary.seng300.tests;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.*;
import ca.ucalgary.seng300.platform.domain.Record;
import ca.ucalgary.seng300.platform.services.LeaderBoardService;
import ca.ucalgary.seng300.platform.services.MatchMakingService;
import ca.ucalgary.seng300.platform.store.*;
import ca.ucalgary.seng300.platform.utils.Helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;


public class MainAppTest {
    public static void main(String[] args) {
//        launch(args);
        LocalDateTime now = LocalDateTime.now(); //local time mountain
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        UserStore userStorage = UserStore.getInstance();
        userStorage.create(new User(1, "Daniel", "daniel.maximov@ucalgary.ca", Helper.hashPassword("ABC", Constants.HASH_ALGORITHM)));
        userStorage.create(new User(2, "Alex", "alex.smith@ucalgary.ca", Helper.hashPassword("DEF", Constants.HASH_ALGORITHM)));
        userStorage.create(new User(3, "John", "John.Johnson@ucalgary,ca", Helper.hashPassword("ABC", Constants.HASH_ALGORITHM)));
        User paul =  new User(4, "Paul", "Paul.C@ucalgary.ca", Helper.hashPassword("jdshakjdbskj", Constants.HASH_ALGORITHM));
        userStorage.create(paul);
//        userStorage.delete(paul);
        System.out.println(userStorage.findByUserName("Alex").getFirst().getEmail());

//        //show that email addresses are true and legit:
        System.out.println(Helper.isEmailValid("daniel.maximov@ucalgary.ca", Constants.EMAIL_REGEX));
        System.out.println(Helper.isEmailValid("\"john.doe\"@example.com", Constants.EMAIL_REGEX));
        System.out.println(Helper.isEmailValid("user=name@example.com", Constants.EMAIL_REGEX));

        for (int i = 1; i < 4; i++){
            System.out.println(Record.getTimeCreatedReadableFormat(userStorage.get(i).getTimeCreated()));
        }


        SessionHistoryStore sessionHistoryStore = SessionHistoryStore.getInstance();
//        sessionHistoryStore.create(new SessionHistory(1));
//        sessionHistoryStore.create(new SessionHistory(2));
//        sessionHistoryStore.create(new SessionHistory(3));
//        sessionHistoryStore.create(new SessionHistory(4));

//        GameHistoryStore gameHistoryStore = new GameHistoryStore(Constants.GAME_FILE_PATH);
        GameHistoryStore gameHistoryStore = GameHistoryStore.getInstance();
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
        ParticipantStore participantStore = ParticipantStore.getInstance();
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

        PlayerStatsStore playerStatsStore = PlayerStatsStore.getInstance();
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
        LeaderBoardService lbs = new LeaderBoardService();
//        lbs.

        List<PlayerStats> sortedPlayers = lbs.getLeaderBoard(false, 1, 4);

        for (int i = 0; i < sortedPlayers.size(); i++){
            System.out.println(sortedPlayers.get(i).getUserID());
            System.out.println(Helper.getRank(sortedPlayers.get(i).getGameCounter(), sortedPlayers.get(i).getScore()).getDisplayName());
        }

// MatchMakingService Tests
        PlayerStatsStore mmStatsStore = PlayerStatsStore.getInstance();
        MatchMakingService matchMaking = new MatchMakingService();

// create players with known scores for TicTacToe
// player 101: score = 50.0 (10 updates of 5.0)
// player 102: score = 53.0 (10 updates of 5.3)  <- closest to 101, diff = 3.0
// player 103: score = 90.0 (10 updates of 9.0)  <- too far from 101 and 102
        PlayerStats mmPlayer1 = new PlayerStats(101, Game.TICK_TACK_TOE, 101);
        PlayerStats mmPlayer2 = new PlayerStats(102, Game.TICK_TACK_TOE, 102);
        PlayerStats mmPlayer3 = new PlayerStats(103, Game.TICK_TACK_TOE, 103);
        PlayerStats mmPlayer4 = new PlayerStats(104, Game.CONNECT_FOUR, 104);
        PlayerStats mmPlayer5 = new PlayerStats(105, Game.CONNECT_FOUR, 105);

        for (int i = 0; i < 10; i++) mmPlayer1.updateStats(5.0);   // score = 50.0
        for (int i = 0; i < 10; i++) mmPlayer2.updateStats(5.3);   // score = 53.0
        for (int i = 0; i < 10; i++) mmPlayer3.updateStats(9.0);   // score = 90.0
        for (int i = 0; i < 10; i++) mmPlayer4.updateStats(5.0);   // score = 50.0
        for (int i = 0; i < 10; i++) mmPlayer5.updateStats(5.5);   // score = 55.0

        mmStatsStore.create(mmPlayer1);
        mmStatsStore.create(mmPlayer2);
        mmStatsStore.create(mmPlayer3);
        mmStatsStore.create(mmPlayer4);
        mmStatsStore.create(mmPlayer5);

// --- Test 1: addPlayerToQueue adds players correctly ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue("101", "TicTacToe");
        matchMaking.addPlayerToQueue("102", "TicTacToe");
        matchMaking.addPlayerToQueue("103", "TicTacToe");
        System.out.println("Test 1 - Queue size should be 3: " + matchMaking.getQueueSize("TicTacToe"));

// --- Test 2: removePlayer removes correctly ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue("101", "TicTacToe");
        matchMaking.addPlayerToQueue("102", "TicTacToe");
        MatchMakingService.removePlayer("102");
        System.out.println("Test 2 - Queue size after remove should be 1: " + matchMaking.getQueueSize("TicTacToe"));

// --- Test 3: matchPlayers returns the closest pair ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue("101", "TicTacToe"); // score is 50
        matchMaking.addPlayerToQueue("102", "TicTacToe"); // score is 53
        matchMaking.addPlayerToQueue("103", "TicTacToe"); // score is 90
        List<String> matched = matchMaking.matchPlayers("TicTacToe");
        System.out.println("Test 3 - Matched players should be [101, 102] (diff=3, closest): " + matched);

// --- Test 4: matched players are removed from queue after match ---
        System.out.println("Test 4 - Queue size after match should be 1 (player 103 remains): " + matchMaking.getQueueSize("TicTacToe"));

// --- Test 5: no match when all score differences exceed 10 ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue("101", "TicTacToe"); // score 50
        matchMaking.addPlayerToQueue("103", "TicTacToe"); // score 90, diff = 40
        List<String> noMatch = matchMaking.matchPlayers("TicTacToe");
        System.out.println("Test 5 - No match when scores too far apart, should be null: " + noMatch);

// --- Test 6: matchPlayers on empty queue returns null ---
        MatchMakingService.queues.clear();
        List<String> emptyMatch = matchMaking.matchPlayers("TicTacToe");
        System.out.println("Test 6 - Match on empty queue should be null: " + emptyMatch);

// --- Test 7: queues for different game types don't interfere ---
        MatchMakingService.queues.clear();
        matchMaking.addPlayerToQueue("101", "TicTacToe");
        matchMaking.addPlayerToQueue("104", "ConnectFour");
        matchMaking.addPlayerToQueue("105", "ConnectFour");
        System.out.println("Test 7 - TicTacToe queue size should be 1: " + matchMaking.getQueueSize("TicTacToe"));
        System.out.println("Test 7 - ConnectFour queue size should be 2: " + matchMaking.getQueueSize("ConnectFour"));


    }
}
