package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.store.PlayerStatsStore;
import ca.ucalgary.seng300.platform.utils.Helper;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LeaderBoardService {
    private final Map<Integer,List<PlayerStats>> cachedLeaderBoard;
    private final Map<Integer, Long> cachedLeaderBoardTS;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final long TEN_MINUTES = 10*60*1000;
    private static final long FORCE_RECALC_THRESHOLD = 15*1000; // if force recalc is called it will recalc if the timestamp is older than 15s

    public LeaderBoardService(){
        cachedLeaderBoard = new HashMap<>();
        cachedLeaderBoardTS = new HashMap<>();
    }

    private static class LeaderBoardHolder{
        private static final LeaderBoardService INSTANCE = new LeaderBoardService();
    }

    public static LeaderBoardService getInstance(){
        return LeaderBoardHolder.INSTANCE;
    }

    private void buildLeaderBoard(int gameId){
        PlayerStatsStore playerStatsStore = PlayerStatsStore.getInstance();
        List<PlayerStats> players = new ArrayList<>(playerStatsStore.findPlayersByGameID(gameId));
        players.sort(Comparator
                .comparingInt(PlayerStats::getElo).reversed()
                .thenComparing((PlayerStats p) -> -Helper.getRank(p.getGameCounter(), p.getScore()).getRankValue())
                .thenComparingDouble(PlayerStats::getScore).reversed()
                .thenComparingInt(PlayerStats::getGameCounter)
        );
        cachedLeaderBoard.put(gameId, players);
        cachedLeaderBoardTS.put(gameId, System.currentTimeMillis());
    }

    private Long getTimeStampSafe(int gameId){
        lock.readLock().lock();
        try{
            return cachedLeaderBoardTS.get(gameId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<PlayerStats> getLeaderBoard(boolean forceRecalc, int gameId, int count) {
        Long ts = getTimeStampSafe(gameId);
        if(forceRecalc || ts == null || System.currentTimeMillis() - ts >= TEN_MINUTES){
            //only one thread can write
            lock.writeLock().lock();
            try {
                Long currentTs = cachedLeaderBoardTS.get(gameId);
                if (currentTs == null || ((forceRecalc && System.currentTimeMillis() - currentTs >= FORCE_RECALC_THRESHOLD)
                        || (System.currentTimeMillis() - currentTs >= TEN_MINUTES))) //is there still no cache? if so rebuilt it OR if the timestamp is too old
                    buildLeaderBoard(gameId);
            } finally{
                lock.writeLock().unlock();
            }
        }
        lock.readLock().lock();
        try {
            List<PlayerStats> list = cachedLeaderBoard.get(gameId);
            if (list == null) {
                return new ArrayList<>();
            }
            return getSublistSafe(list, count);
        } finally {
            lock.readLock().unlock();
        }
    }

    private static List<PlayerStats> getSublistSafe(List<PlayerStats> orig, int count) {
        if (orig == null || orig.isEmpty()) {
            return new ArrayList<>();
        }
        if (count > orig.size()) {
            count = orig.size();
        }
        return new ArrayList<>(orig.subList(0, count));
    }
}
