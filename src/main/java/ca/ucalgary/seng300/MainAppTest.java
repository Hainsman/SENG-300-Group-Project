package ca.ucalgary.seng300;

import ca.ucalgary.seng300.platform.constants.Constants;
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



        Store<User> userStorage = new Store<User>(Constants.USERFILEPATH);
        userStorage.create(new User(1, "Daniel", "daniel.maximov@ucalgary.ca", Helper.hashPassword("ABC", Constants.HASHALGORITHM)));
        userStorage.create(new User(2, "Alex", "alex.smith@ucalgary.ca", Helper.hashPassword("DEF", Constants.HASHALGORITHM)));
        userStorage.create(new User(3, "John", "John.Johnson@ucalgary,ca", Helper.hashPassword("ABC", Constants.HASHALGORITHM)));
        User paul =  new User(4, "Paul", "Paul.C@ucalgary.ca", Helper.hashPassword("jdshakjdbskj", Constants.HASHALGORITHM));
        userStorage.create(paul);
        userStorage.delete(paul);

        for (var id: userStorage.records.keySet()){
            System.out.println(id);
            User user = userStorage.records.get(id);
            System.out.println("ID: " + id.toString() + ", User: " + user.getlogin() + ", Password: " + user.getPasswordHash());

        }

        Store<GameHistory> gameHistoryStorage = new Store<GameHistory>(Constants.GAMEFILEPATH);
        gameHistoryStorage.create(new GameHistory(1, 1));
        gameHistoryStorage.create(new GameHistory(2, 2));
        gameHistoryStorage.create(new GameHistory(3, 3));
        GameHistory gameHistory1 = new GameHistory(4,4);
        gameHistoryStorage.create(gameHistory1);

        for (var id: gameHistoryStorage.records.keySet()){
            System.out.println(id);
            GameHistory gameHistory = gameHistoryStorage.records.get(id);
            System.out.println("Game History ID: " + gameHistory.getID() + ", GameID: " + gameHistory.getGameID() + ", TimeID: " + gameHistory.getTimeCreated());

        }


        var identicalGH = gameHistoryStorage.find( rec -> rec.getGameID() == 3);
        System.out.println(identicalGH.hashCode());

        //show that email addresses are true and legit:
        System.out.println(Helper.isEmailValid("daniel.maximov@ucalgary.ca", Constants.EMAILREGEX));
        System.out.println(Helper.isEmailValid("\"john.doe\"@example.com", Constants.EMAILREGEX));
        System.out.println(Helper.isEmailValid("user=name@example.com", Constants.EMAILREGEX));

        Store<SessionHistory> SessionHistoryStorage = new Store<SessionHistory>(Constants.SESSIONFILEPATH);
        SessionHistoryStorage.create(new SessionHistory(1));
        SessionHistoryStorage.create(new SessionHistory(2));
        SessionHistoryStorage.create(new SessionHistory(3));
        SessionHistoryStorage.create(new SessionHistory(4));

        Store<Participants> participantsStore = new Store<Participants>(Constants.PARTICIPANTSFILEPATH);
        participantsStore.create(new Participants(1, 1, 1, 10));
        participantsStore.create(new Participants(2, 1, 2, 5));
        participantsStore.create(new Participants(3, 1, 3, 34));
        participantsStore.create(new Participants(4, 1, 4, 42));

    }
}
