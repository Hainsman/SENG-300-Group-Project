package ca.ucalgary.seng300.ui.util;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.ui.centralcontroller.CentralController;

public final class PlayerProfileHelper {

    private PlayerProfileHelper() {}

    public static int currentUserIdOrDefault(CentralController rootController) {
        int id = CentralController.currentUserId;
        return id != 0 ? id : 1;
    }

    /** ELO for the logged-in user from persisted {@link User} data. */
    public static int eloForGameType(GameType gameType, CentralController rootController) {
        User u = UserStore.getInstance().get(currentUserIdOrDefault(rootController));
        // TODO fix this
//        return u == null ? 1200 : u.getEloForGame(gameType);
        return 1200;
    }

    public static int gamesForGameType(GameType gameType, CentralController rootController) {
        User u = UserStore.getInstance().get(currentUserIdOrDefault(rootController));
        // TODO fix this
//        return u == null ? 0 : u.getGamesForGame(gameType);
        return 0;
    }

    /**
     * Backwards-compatible view backed by {@link User} rows (not PlayerStatsStore).
     * Mutating the returned object does not persist; use {@link UserStore#update(User)}.
     */
    public static PlayerStats statsForGameType(GameType gameType) {
        // TODO fix this
//        int uid = currentUserIdOrDefault();
//        User u = UserStore.getInstance().get(uid);
//        PlayerStats ps = new PlayerStats(0, gameType, uid);
//        if (u != null) {
//            ps.setElo(u.getEloForGame(gameType));
//            for (int i = 0; i < u.getGamesForGame(gameType); i++) {
//                ps.updateStats(0);
//            }
//        }
//        return ps;
        return null;
    }

    public static PlayerStats statsForCurrentGame(CentralController rootController) {
        return statsForGameType(CentralController.selectedGame);
    }

    public static String sanitizeForPayload(String name) {
        if (name == null) {
            return "Player";
        }
        String t = name.trim().replace('|', ' ').replace('\n', ' ');
        return t.isEmpty() ? "Player" : t;
    }

    public static String lobbyPayload(CentralController rootController) {
        String name = sanitizeForPayload(CentralController.displayName);
        int elo = eloForGameType(CentralController.selectedGame, rootController);
        return name + "|" + elo;
    }

    public static String eloTierLabel(int elo) {
        if (elo < 1000) {
            return "Novice";
        }
        if (elo < 1200) {
            return "Bronze";
        }
        if (elo < 1400) {
            return "Silver";
        }
        if (elo < 1600) {
            return "Gold";
        }
        if (elo < 1800) {
            return "Platinum";
        }
        return "Master";
    }

    /** Short badge text for UI (e.g. "Au"). */
    public static String eloTierBadge(int elo) {
        return switch (eloTierLabel(elo)) {
            case "Novice" -> "Nv";
            case "Bronze" -> "Br";
            case "Silver" -> "Ag";
            case "Gold" -> "Au";
            case "Platinum" -> "Pt";
            default -> "M";
        };
    }
}
