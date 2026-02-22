package ca.ucalgary.seng300;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.Store;
import ca.ucalgary.seng300.platform.utils.Helper;


public class MainAppTest {
    public static void main(String[] args) {
//        launch(args);
        Store<User> userStorage = new Store<User>(Constants.USERFILEPATH);
        userStorage.create(new User(1, "Daniel", Helper.hashPassword("ABC", Constants.HASHALGORITHM)));
        userStorage.create(new User(2, "Alex", Helper.hashPassword("DEF", Constants.HASHALGORITHM)));
        userStorage.create(new User(3, "John", Helper.hashPassword("ABC", Constants.HASHALGORITHM)));
        User paul =  new User(4, "Paul", Helper.hashPassword("jdshakjdbskj", Constants.HASHALGORITHM));
        userStorage.create(paul);
        userStorage.delete(paul);

        for (var id: userStorage.records.keySet()){
            System.out.println(id);
            User user = userStorage.records.get(id);
            System.out.println("ID: " + id.toString() + ", User: " + user.getlogin() + ", Password: " + user.getPasswordHash());

        }

        Store<GameHistory> gameHistoryStorage = new Store<GameHistory>(Constants.GAMEFILEPATH);
        gameHistoryStorage.create(new GameHistory(1, 1, 1));
        gameHistoryStorage.create(new GameHistory(2, 2, 2));
        gameHistoryStorage.create(new GameHistory(3, 3, 3));
        GameHistory gameHistory1 = new GameHistory(4,4, 4);
        gameHistoryStorage.create(gameHistory1);

        for (var id: gameHistoryStorage.records.keySet()){
            System.out.println(id);
            GameHistory gameHistory = gameHistoryStorage.records.get(id);
            System.out.println("Game History ID: " + gameHistory.getID() + ", GameID: " + gameHistory.getGameID() + ", TimeID: " + gameHistory.getTimeID());

        }

        var identicalGH = gameHistoryStorage.find( rec -> rec.getGameID() == 3);
        System.out.println(identicalGH.get(0).toString());

    }

}
