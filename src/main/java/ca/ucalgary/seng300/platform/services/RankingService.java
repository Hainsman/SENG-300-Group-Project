package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.UserStore;

public final class RankingService {

    private static final int K = 16;

    public enum OnlineOutcome {
        WIN, LOSS, DRAW
    }

    public record OnlineEloResult(int deltaElo, int newElo, OnlineOutcome outcome) {}

    public record MatchEloResult(
            int playerADelta,
            int playerANewElo,
            int playerBDelta,
            int playerBNewElo,
            OnlineOutcome outcomeForA) {}

    private RankingService() {}

    private static int calculateDelta(int playerElo, int opponentElo, OnlineOutcome outcome) {
        double expected = 1.0 / (1.0 + Math.pow(10, (opponentElo - playerElo) / 400.0));
        double actual = switch (outcome) {
            case WIN -> 1.0;
            case LOSS -> 0.0;
            case DRAW -> 0.5;
        };
        return (int) Math.round(K * (actual - expected));
    }

    public static OnlineEloResult applyOnlineLocalResult(
            int localUserId, GameType gameType, int opponentElo, OnlineOutcome outcome) {
        UserStore store = UserStore.getInstance();
        User me = store.get(localUserId);
        if (me == null) {
            return new OnlineEloResult(0, 1200, outcome);
        }
        int myElo = me.getEloForGame(gameType);
        int delta = calculateDelta(myElo, opponentElo, outcome);
        me.adjustEloForGame(gameType, delta);
        me.incrementGamesForGame(gameType);
        store.update(me);
        return new OnlineEloResult(delta, me.getEloForGame(gameType), outcome);
    }

    public static MatchEloResult applyMatchResult(
            int playerAId, int playerBId, GameType gameType, OnlineOutcome outcomeForA) {
        UserStore store = UserStore.getInstance();
        User playerA = store.get(playerAId);
        User playerB = store.get(playerBId);
        if (playerA == null || playerB == null) {
            return new MatchEloResult(0, 1200, 0, 1200, outcomeForA);
        }
        int eloA = playerA.getEloForGame(gameType);
        int eloB = playerB.getEloForGame(gameType);
        int deltaA = calculateDelta(eloA, eloB, outcomeForA);
        OnlineOutcome outcomeForB = switch (outcomeForA) {
            case WIN -> OnlineOutcome.LOSS;
            case LOSS -> OnlineOutcome.WIN;
            case DRAW -> OnlineOutcome.DRAW;
        };
        int deltaB = calculateDelta(eloB, eloA, outcomeForB);

        playerA.adjustEloForGame(gameType, deltaA);
        playerB.adjustEloForGame(gameType, deltaB);
        playerA.incrementGamesForGame(gameType);
        playerB.incrementGamesForGame(gameType);
        store.update(playerA);
        store.update(playerB);

        return new MatchEloResult(
                deltaA,
                playerA.getEloForGame(gameType),
                deltaB,
                playerB.getEloForGame(gameType),
                outcomeForA);
    }
}
