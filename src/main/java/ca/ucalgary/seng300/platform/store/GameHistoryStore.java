package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.domain.GameHistory;

import java.util.List;

public class GameHistoryStore extends Store<GameHistory> {
    public GameHistoryStore(String filePath) {super(filePath);}

    //retrieves all instances of games given a GameID
    public List<GameHistory> findByGameID(GameType gameType){return find(rec -> rec.getGameType() == gameType);}
    // Bill Pugh Singleton
    private static class GameHistoryStoreHolder{
        private static final GameHistoryStore INSTANCE = new GameHistoryStore(Constants.GAME_FILE_PATH);
    }
    public static GameHistoryStore getInstance(){return GameHistoryStoreHolder.INSTANCE;}

}
