package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.UserStore;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LeaderBoardService {
    private final Map<Integer, List<User>> cachedLeaderBoard;
    private final Map<Integer, Long> cachedLeaderBoardTS;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final long TEN_MINUTES = 10 * 60 * 1000;
    private static final long FORCE_RECALC_THRESHOLD = 15 * 1000;

    public LeaderBoardService() {
        cachedLeaderBoard = new HashMap<>();
        cachedLeaderBoardTS = new HashMap<>();
    }

    private static class LeaderBoardHolder {
        private static final LeaderBoardService INSTANCE = new LeaderBoardService();
    }

    public static LeaderBoardService getInstance() {
        return LeaderBoardHolder.INSTANCE;
    }

    private void buildLeaderBoard(int gameId) {
        List<User> users = new ArrayList<>(UserStore.getInstance().findAll());
        users.sort(Comparator
                .comparingInt((User u) -> u.getEloForGame(gameId)).reversed()
                .thenComparing(User::getlogin, String.CASE_INSENSITIVE_ORDER));
        cachedLeaderBoard.put(gameId, users);
        cachedLeaderBoardTS.put(gameId, System.currentTimeMillis());
    }

    private Long getTimeStampSafe(int gameId) {
        lock.readLock().lock();
        try {
            return cachedLeaderBoardTS.get(gameId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<User> getLeaderBoard(boolean forceRecalc, int gameId, int count) {
        Long ts = getTimeStampSafe(gameId);
        if (forceRecalc || ts == null || System.currentTimeMillis() - ts >= TEN_MINUTES) {
            lock.writeLock().lock();
            try {
                Long currentTs = cachedLeaderBoardTS.get(gameId);
                if (currentTs == null
                        || ((forceRecalc && System.currentTimeMillis() - currentTs >= FORCE_RECALC_THRESHOLD)
                        || (System.currentTimeMillis() - currentTs >= TEN_MINUTES))) {
                    buildLeaderBoard(gameId);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        lock.readLock().lock();
        try {
            List<User> list = cachedLeaderBoard.get(gameId);
            if (list == null) {
                return new ArrayList<>();
            }
            return getSublistSafe(list, count);
        } finally {
            lock.readLock().unlock();
        }
    }

    private static List<User> getSublistSafe(List<User> orig, int count) {
        if (orig == null || orig.isEmpty()) {
            return new ArrayList<>();
        }
        if (count > orig.size()) {
            count = orig.size();
        }
        return new ArrayList<>(orig.subList(0, count));
    }
}
