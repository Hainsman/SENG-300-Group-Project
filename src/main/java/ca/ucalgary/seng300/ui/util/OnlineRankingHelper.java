package ca.ucalgary.seng300.ui.util;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.services.RankingService;
import ca.ucalgary.seng300.ui.CentralController;

import java.util.Optional;

public final class OnlineRankingHelper {

    private OnlineRankingHelper() {}

    // TODO FIX
    /*public static int opponentEloFromLobby(boolean hostingLobby) {
        int e = hostingLobby ? CentralController.rosterGuestElo : CentralController.rosterHostElo;
        return e <= 0 ? 1200 : e;
    }*/

    public static Optional<RankingService.OnlineEloResult> applyIfOnlineGameFinished(
            ClientCore cc, State state, boolean alreadyApplied, GameType gameTypeForStats) {
        if (alreadyApplied || cc == null || !cc.isConnected()) {
            return Optional.empty();
        }
        Player myRole = cc.getMyNetworkRole();
        if (myRole == null || State.IN_PROGRESS.equals(state)) {
            return Optional.empty();
        }
        RankingService.OnlineOutcome outcome;
        if (State.TIE.equals(state)) {
            outcome = RankingService.OnlineOutcome.DRAW;
        } else if (State.PLAYER_1_WIN.equals(state)) {
            outcome = myRole == Player.PLAYER_1
                    ? RankingService.OnlineOutcome.WIN
                    : RankingService.OnlineOutcome.LOSS;
        } else if (State.PLAYER_2_WIN.equals(state)) {
            outcome = myRole == Player.PLAYER_2
                    ? RankingService.OnlineOutcome.WIN
                    : RankingService.OnlineOutcome.LOSS;
        } else {
            return Optional.empty();
        }
        int uid = PlayerProfileHelper.currentUserIdOrDefault(null);
        //int oppElo = opponentEloFromLobby(CentralController.hostingLobby);
        //return Optional.of(RankingService.applyOnlineLocalResult(uid, gameTypeForStats, oppElo, outcome));
        return null;
    }
}
