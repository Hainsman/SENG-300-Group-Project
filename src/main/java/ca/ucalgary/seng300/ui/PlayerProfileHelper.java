package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.store.PlayerStatsStore;

public final class PlayerProfileHelper {

    private PlayerProfileHelper() {}

    public static int currentUserIdOrDefault() {
        int id = CentralController.currentUserId;
        return id != 0 ? id : 1;
    }

    public static PlayerStats statsForGameId(int gameId) {
        int uid = currentUserIdOrDefault();
        return PlayerStatsStore.getInstance().getOrCreateForUserGame(uid, gameId);
    }

    public static PlayerStats statsForCurrentGame() {
        return statsForGameId(GameIdMapper.fromUiGameName(CentralController.selectedGame));
    }

    public static int eloForGameId(int gameId) {
        return statsForGameId(gameId).getElo();
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
        int elo = eloForGameId(GameIdMapper.fromUiGameName(CentralController.selectedGame));
        return name + "|" + elo;
    }

    public static String eloTierLabel(int elo) {
        if (elo < 1000) return "Novice";
        if (elo < 1200) return "Bronze";
        if (elo < 1400) return "Silver";
        if (elo < 1600) return "Gold";
        if (elo < 1800) return "Platinum";
        return "Master";
    }
}
