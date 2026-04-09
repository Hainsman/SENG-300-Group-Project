package ca.ucalgary.seng300.ui.util;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.ui.CentralController;

public final class PlayerProfileHelper {

    private PlayerProfileHelper() {}

    public static int currentUserIdOrDefault() {
        int id = CentralController.currentUserId;
        return id > 0 ? id : 0;
    }

    /** ELO for the logged-in user from persisted {@link User} data. */
    public static int eloForGameType(GameType gameType) {
        User u = UserStore.getInstance().get(currentUserIdOrDefault());
        return u == null ? 1200 : u.getEloForGame(gameType);
    }

    public static int gamesForGameType(GameType gameType) {
        User u = UserStore.getInstance().get(currentUserIdOrDefault());
        return u == null ? 0 : u.getGamesForGame(gameType);
    }

    /**
     * Backwards-compatible view backed by {@link User} rows (not PlayerStatsStore).
     * Mutating the returned object does not persist; use {@link UserStore#update(User)}.
     */
    public static PlayerStats statsForGameType(GameType gameType) {
        int uid = currentUserIdOrDefault();
        User u = UserStore.getInstance().get(uid);
        PlayerStats ps = new PlayerStats(0, gameType, uid);
        if (u != null) {
            ps.setElo(u.getEloForGame(gameType));
            for (int i = 0; i < u.getGamesForGame(gameType); i++) {
                ps.updateStats(0);
            }
        }
        return ps;
    }

    public static PlayerStats statsForCurrentGame() {
        return statsForGameType(CentralController.selectedGame);
    }

    public static String sanitizeForPayload(String name) {
        if (name == null) {
            return "Player";
        }
        String t = name.trim().replace('|', ' ').replace('\n', ' ');
        return t.isEmpty() ? "Player" : t;
    }

    public static String lobbyPayload() {
        String name = sanitizeForPayload(CentralController.displayName);
        int elo = eloForGameType(CentralController.selectedGame);
        return name + "|" + elo;
    }

    public static String eloTierLabel(int elo) {
        return Rank.fromElo(elo).getDisplayName();
    }

    /** Short badge text for UI (e.g. "Br" or "GM"). */
    public static String eloTierBadge(int elo) {
        return Rank.fromElo(elo).getBadge();
    }
}
