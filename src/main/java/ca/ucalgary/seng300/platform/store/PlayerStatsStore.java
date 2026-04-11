package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.utils.Helper;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerStatsStore extends Store<PlayerStats> {
    public PlayerStatsStore(String filePath) {
        super(filePath);
    }
    //returns users by gameID
    public List<PlayerStats> findPlayersByGameID (int id){return find(rec -> rec.getGameID() == id);}
    // Bill Pugh Singleton
    private static class PlayerStatsStoreHolder{
        private static final PlayerStatsStore INSTANCE = new PlayerStatsStore(Constants.PLAYER_STATS_FILE_PATH);
    }
    public static PlayerStatsStore getInstance() {return PlayerStatsStoreHolder.INSTANCE;}
}
