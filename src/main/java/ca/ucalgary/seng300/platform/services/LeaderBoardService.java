package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.store.PlayerStatsStore;
import ca.ucalgary.seng300.platform.utils.Helper;

import java.util.*;

public class LeaderBoardService {
    static Map<Integer,List<PlayerStats>> cachedLeaderBoard = new HashMap<Integer,List<PlayerStats>>();
    static Map<Integer, Long> cachedLeaderBoardTS = new HashMap<Integer, Long>();

    public List<PlayerStats> getLeaderBoard(boolean forceRecalc, int gameId, int count, PlayerStatsStore playerStatsStore) {
        var ts = cachedLeaderBoardTS.get(gameId);

        if(!cachedLeaderBoardTS.isEmpty() && (!forceRecalc || ts != null || System.currentTimeMillis() - ts < 10*60*1000))
            return getSublistSafe(cachedLeaderBoard.get(gameId), count);

        List<PlayerStats> players = new ArrayList<>(playerStatsStore.findPlayersByGameID(gameId));

        players.sort(Comparator
                .comparing((PlayerStats p) -> -Helper.getRank(p.getGameCounter(), p.getScore()).getRankValue()) //sort players by rank
                .thenComparing(Comparator.comparingDouble(PlayerStats::getScore).reversed()) //then sort them by score in descending order
                .thenComparing(PlayerStats::getGameCounter) //sort them in descending order  by number of games played
        );
        cachedLeaderBoard.put(gameId, players);
        cachedLeaderBoardTS.put(gameId, System.currentTimeMillis());
        return getSublistSafe(cachedLeaderBoard.get(gameId), count);
    }

    private static List<PlayerStats> getSublistSafe(List<PlayerStats> orig, int count) {
        if (count < orig.size())
            count = orig.size();
        return orig.subList(0, count);
    }
}
