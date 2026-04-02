package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.UserStore;

public final class RankingService {

    private static final int K = 16;

    public enum OnlineOutcome {
        WIN, LOSS, DRAW
    }

    public record OnlineEloResult(int deltaElo, int newElo, OnlineOutcome outcome) {}

    private RankingService() {}

    public static OnlineEloResult applyOnlineLocalResult(
            int localUserId, int gameId, int opponentElo, OnlineOutcome outcome) {
        UserStore store = UserStore.getInstance();
        User me = store.get(localUserId);
        if (me == null) {
            return new OnlineEloResult(0, 1200, outcome);
        }
        int myElo = me.getEloForGame(gameId);
        double expected = 1.0 / (1.0 + Math.pow(10, (opponentElo - myElo) / 400.0));
        double actual = switch (outcome) {
            case WIN -> 1.0;
            case LOSS -> 0.0;
            case DRAW -> 0.5;
        };
        int delta = (int) Math.round(K * (actual - expected));
        me.adjustEloForGame(gameId, delta);
        me.incrementGamesForGame(gameId);
        store.update(me);
        return new OnlineEloResult(delta, me.getEloForGame(gameId), outcome);
    }
}
