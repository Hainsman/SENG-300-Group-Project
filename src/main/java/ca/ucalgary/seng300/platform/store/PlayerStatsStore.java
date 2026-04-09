package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.domain.PlayerStats;

import java.util.List;

public class PlayerStatsStore extends Store<PlayerStats> {
    public PlayerStatsStore(String filePath) {
        super(filePath);
    }

    //returns users by gameID
    public List<PlayerStats> findPlayersByGameType(GameType gameType) {
        return find(rec -> rec.getGameType() == gameType);
    }

    public List<PlayerStats> findByUserAndGame(int userId, GameType gameType) {
        return find(rec -> rec.getUserID() == userId && rec.getGameType() == gameType);
    }

    public int nextId() {
        return find(rec -> true).stream().mapToInt(PlayerStats::getID).max().orElse(0) + 1;
    }

    public PlayerStats getOrCreateForUserGame(int userId, GameType gameType) {
        List<PlayerStats> matches = findByUserAndGame(userId, gameType);
        if (!matches.isEmpty()) {
            return matches.get(0);
        }
        PlayerStats ps = new PlayerStats(nextId(), gameType, userId);
        create(ps);
        return ps;
    }

    public List<PlayerStats> findPlayersByGameID(int gameId) {
        return null;
    }

    // Bill Pugh Singleton
    private static class PlayerStatsStoreHolder{
        private static final PlayerStatsStore INSTANCE = new PlayerStatsStore(Constants.PLAYER_STATS_FILE_PATH);
    }
    public static PlayerStatsStore getInstance() {return PlayerStatsStoreHolder.INSTANCE;}
}
