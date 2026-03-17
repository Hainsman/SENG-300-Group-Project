package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.utils.Helper;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerStatsStore extends Store<PlayerStats> {
    public PlayerStatsStore(String filePath) {
        super(filePath);
    }
    //returns users by gameID
    public static List<PlayerStats> findPlayersByGameID(int id){return find(rec -> rec.getGameID() == id);}
}
