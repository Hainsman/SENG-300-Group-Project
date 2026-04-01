package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.constants.Game;

public final class GameIdMapper {

    private GameIdMapper() {}

    public static int fromUiGameName(String selected) {
        if (selected == null) {
            return Game.TICK_TACK_TOE;
        }
        String lower = selected.toLowerCase();
        if (lower.contains("connect") || lower.contains("c4")) {
            return Game.CONNECT_FOUR;
        }
        return Game.TICK_TACK_TOE;
    }

    public static String toServerGameKey(String selected) {
        return fromUiGameName(selected) == Game.CONNECT_FOUR ? "C4" : "TTT";
    }
}
