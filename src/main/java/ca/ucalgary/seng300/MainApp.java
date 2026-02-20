package ca.ucalgary.seng300;

import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.Store;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("JavaFX 25 + JDK 25 OK");
        Scene scene = new Scene(label, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Starter");
        stage.show();
        Store<User> userStorage = new Store<User>("user.txt");
        userStorage.create(new User(1, "Daniel", "123"));
        userStorage.create(new User(2, "Alex", "456"));
        userStorage.create(new User(3, "John", "789"));
        User paul =  new User(4, "Paul", "101112");
        userStorage.create(paul);
        userStorage.delete(paul);

        for (var id: userStorage.records.keySet()){
            System.out.println(id);
            User user = userStorage.records.get(id);
            System.out.println("ID: " + id.toString() + ", User: " + user.getlogin() + ", Password: " + user.getPasswordHash());

        }

        Store<GameHistory> gameHistoryStorage = new Store<GameHistory>("gamehistory.txt");
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

    public static void main(String[] args) {
        launch(args);
    }

}
