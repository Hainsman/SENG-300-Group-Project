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

    private RankingService() {}

    public static OnlineEloResult applyOnlineLocalResult(
            int localUserId, GameType gameType, int opponentElo, OnlineOutcome outcome) {
        UserStore store = UserStore.getInstance();
        User me = store.get(localUserId);
        if (me == null) {
            return new OnlineEloResult(0, 1200, outcome);
        }
        // Per-game ELO on User is tracked via PlayerStats in the full platform; keep stub for compile.
        int myElo = 1200;
        double expected = 1.0 / (1.0 + Math.pow(10, (opponentElo - myElo) / 400.0));
        double actual = switch (outcome) {
            case WIN -> 1.0;
            case LOSS -> 0.0;
            case DRAW -> 0.5;
        };
        int delta = (int) Math.round(K * (actual - expected));
        return new OnlineEloResult(delta, myElo + delta, outcome);
    }
}
