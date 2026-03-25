package ca.ucalgary.seng300.tests;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.constants.TypeSession;
import ca.ucalgary.seng300.platform.domain.*;
import ca.ucalgary.seng300.platform.domain.Record;
import ca.ucalgary.seng300.platform.messages.CreateOrJoinSession;
import ca.ucalgary.seng300.platform.messages.GameStateUpdate;
import ca.ucalgary.seng300.platform.messages.Message;
import ca.ucalgary.seng300.platform.messages.SessionCreated;
import ca.ucalgary.seng300.platform.messages.SubmitMove;
import ca.ucalgary.seng300.platform.services.LeaderBoardService;
import ca.ucalgary.seng300.platform.store.*;
import ca.ucalgary.seng300.platform.utils.Helper;
import java.util.List;
import java.util.UUID;

import static ca.ucalgary.seng300.platform.messages.Message.toJson;


public class MainAppTest {
    public static void main(String[] args) {

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

        UUID uniuqeIdent = UUID.randomUUID();

        CreateOrJoinSession createOrJoinSession = new CreateOrJoinSession(TypeSession.CREATE_SESSION, uniuqeIdent, Game.TICK_TACK_TOE);
        GameStateUpdate gameStateUpdate = new GameStateUpdate(uniuqeIdent, new TTTGameState(), "");
        SessionCreated sessionCreated = new SessionCreated(uniuqeIdent, Player.PLAYER_1, "");
        SubmitMove submitMove = new SubmitMove(uniuqeIdent, 1, 0, Game.CONNECT_FOUR);

        String JsonCreateOrJoinSession = toJson(createOrJoinSession);
        String JsonGameStateUpdate = toJson(gameStateUpdate);
        String JsonSessionCreated = toJson(sessionCreated);
        String JsonsubmitMove = toJson(submitMove);

        System.out.println(JsonCreateOrJoinSession);
        System.out.println(JsonGameStateUpdate);
        System.out.println(JsonSessionCreated);
        System.out.println(JsonsubmitMove);

        Message messageCreateOrJoin = createOrJoinSession.fromJson(JsonCreateOrJoinSession);
        Message messageGameStateUpdate = gameStateUpdate.fromJson(JsonGameStateUpdate);
        Message messageSessionCreated = sessionCreated.fromJson(JsonSessionCreated);
        Message messageSubmitMove = submitMove.fromJson(JsonsubmitMove);

        System.out.println(messageCreateOrJoin.getGloballyUniqueSessionId());
        System.out.println(messageGameStateUpdate.getGloballyUniqueSessionId());
        System.out.println(messageSessionCreated.getGloballyUniqueSessionId());
        System.out.println(messageSubmitMove.getGloballyUniqueSessionId());
    }
}
