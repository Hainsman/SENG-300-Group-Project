package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.store.PlayerStatsStore;

public final class RankingService {

    private static final int K = 16;

    public enum OnlineOutcome {
        WIN, LOSS, DRAW
    }

    public record OnlineEloResult(int deltaElo, int newElo, OnlineOutcome outcome) {}

    private RankingService() {}

    public static OnlineEloResult applyOnlineLocalResult(
            int localUserId, int gameId, int opponentElo, OnlineOutcome outcome) {
        PlayerStatsStore store = PlayerStatsStore.getInstance();
        PlayerStats me = store.getOrCreateForUserGame(localUserId, gameId);
        int myElo = me.getElo();
        double expected = 1.0 / (1.0 + Math.pow(10, (opponentElo - myElo) / 400.0));
        double actual = switch (outcome) {
            case WIN -> 1.0;
            case LOSS -> 0.0;
            case DRAW -> 0.5;
        };
        int delta = (int) Math.round(K * (actual - expected));
        me.adjustElo(delta);
        me.updateStats(actual);
        store.update(me);
        return new OnlineEloResult(delta, me.getElo(), outcome);
    }
}
