package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.PlayerStats;

import java.util.List;

public class PlayerStatsStore extends Store<PlayerStats> {
    public PlayerStatsStore(String filePath) {
        super(filePath);
    }
    //returns users by gameID
    public List<PlayerStats> findPlayersByGameID (int id){return find(rec -> rec.getGameID() == id);}

    public List<PlayerStats> findByUserAndGame(int userId, int gameId) {
        return find(rec -> rec.getUserID() == userId && rec.getGameID() == gameId);
    }

    public int nextId() {
        return find(rec -> true).stream().mapToInt(PlayerStats::getID).max().orElse(0) + 1;
    }

    public PlayerStats getOrCreateForUserGame(int userId, int gameId) {
        List<PlayerStats> matches = findByUserAndGame(userId, gameId);
        if (!matches.isEmpty()) {
            return matches.get(0);
        }
        PlayerStats ps = new PlayerStats(nextId(), gameId, userId);
        create(ps);
        return ps;
    }

    // Bill Pugh Singleton
    private static class PlayerStatsStoreHolder{
        private static final PlayerStatsStore INSTANCE = new PlayerStatsStore(Constants.PLAYER_STATS_FILE_PATH);
    }
    public static PlayerStatsStore getInstance() {return PlayerStatsStoreHolder.INSTANCE;}
}
