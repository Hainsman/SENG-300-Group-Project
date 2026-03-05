package ca.ucalgary.seng300;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.domain.Participants;
import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.Store;
import ca.ucalgary.seng300.platform.utils.Helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class MainAppTest {
    public static void main(String[] args) {
//        launch(args);
        LocalDateTime now = LocalDateTime.now(); //local time mountain
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Store<User> userStorage = new Store<User>(Constants.USER_FILE_PATH);
        userStorage.create(new User(1, "Daniel", "daniel.maximov@ucalgary.ca", Helper.hashPassword("ABC", Constants.HASH_ALGORITHM)));
        userStorage.create(new User(2, "Alex", "alex.smith@ucalgary.ca", Helper.hashPassword("DEF", Constants.HASH_ALGORITHM)));
        userStorage.create(new User(3, "John", "John.Johnson@ucalgary,ca", Helper.hashPassword("ABC", Constants.HASH_ALGORITHM)));
        User paul =  new User(4, "Paul", "Paul.C@ucalgary.ca", Helper.hashPassword("jdshakjdbskj", Constants.HASH_ALGORITHM));
        userStorage.create(paul);
        userStorage.delete(paul);

//        for (var id: userStorage.records.keySet()){
//            System.out.println(id);
//            User user = userStorage.records.get(id);
//            System.out.println("ID: " + id.toString() + ", User: " + user.getlogin() + ", Password: " + user.getPasswordHash());
//
//        }

//        var identicalGH = gameHistoryStorage.find( rec -> rec.getGameID() == 3);
//        System.out.println(identicalGH.hashCode());

        //show that email addresses are true and legit:
        System.out.println(Helper.isEmailValid("daniel.maximov@ucalgary.ca", Constants.EMAIL_REGEX));
        System.out.println(Helper.isEmailValid("\"john.doe\"@example.com", Constants.EMAIL_REGEX));
        System.out.println(Helper.isEmailValid("user=name@example.com", Constants.EMAIL_REGEX));
        Store<SessionHistory> SessionHistoryStorage = new Store<SessionHistory>(Constants.SESSION_FILE_PATH);
        SessionHistoryStorage.create(new SessionHistory(1));
        SessionHistoryStorage.create(new SessionHistory(2));
        SessionHistoryStorage.create(new SessionHistory(3));
        SessionHistoryStorage.create(new SessionHistory(4));

        Store<GameHistory> gameHistoryStorage = new Store<GameHistory>(Constants.GAME_FILE_PATH);
        gameHistoryStorage.create(new GameHistory(1, Game.TICK_TACK_TOE));
        gameHistoryStorage.create(new GameHistory(2, Game.TICK_TACK_TOE));
        gameHistoryStorage.create(new GameHistory(3, Game.TICK_TACK_TOE));
        GameHistory gameHistory1 = new GameHistory(4, Game.TICK_TACK_TOE);
        gameHistoryStorage.create(new GameHistory(5, Game.TICK_TACK_TOE));
        gameHistoryStorage.create(new GameHistory(6, Game.TICK_TACK_TOE));
        gameHistoryStorage.create(new GameHistory(7, Game.TICK_TACK_TOE));
        gameHistoryStorage.create(new GameHistory(8, Game.TICK_TACK_TOE));
        gameHistoryStorage.create(new GameHistory(9, Game.TICK_TACK_TOE));
        gameHistoryStorage.create(new GameHistory(10, Game.TICK_TACK_TOE));
        gameHistoryStorage.create(gameHistory1);

        Store<Participants> participantsStore = new Store<Participants>(Constants.PARTICIPANTS_FILE_PATH);
        participantsStore.create(new Participants(1, 1, 1, 1));
        participantsStore.create(new Participants(2, 2, 1, 1));
        participantsStore.create(new Participants(3, 3, 1, 1));
        participantsStore.create(new Participants(4, 4, 1, 1));
        participantsStore.create(new Participants(5, 5, 1, 1));
        participantsStore.create(new Participants(6, 6, 1, 1));
        participantsStore.create(new Participants(7, 7, 1, 1));
        participantsStore.create(new Participants(8, 8, 1, 1));
        participantsStore.create(new Participants(9, 9, 1, 1));
        participantsStore.create(new Participants(10, 10, 1, 80));

        Rank rank = Helper.scoreToRank(participantsStore, gameHistoryStorage, 1, 1);
        System.out.println("User Rank: "+ rank);

    }
}
