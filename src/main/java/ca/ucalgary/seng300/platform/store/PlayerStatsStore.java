package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.PlayerStats;

import java.util.List;

public class PlayerStatsStore extends Store<PlayerStats> {
    public PlayerStatsStore(String filePath) {
        super(filePath);
    }
    //returns users by gameID
    public List<PlayerStats> findPlayersByGameID (int id){
        return find(rec -> rec.getGameID() == id);
    }
}
