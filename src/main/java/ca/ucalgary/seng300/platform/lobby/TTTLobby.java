package ca.ucalgary.seng300.platform.lobby;

import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.domain.PlayerStats;


import java.util.List;

public class TTTLobby extends Lobby {
    @Override
    public int getGameType() { return Game.TICK_TACK_TOE; }

    @Override
    public void startGame(List<PlayerStats> players) {
        /* TTT specific setup */ }

    // not sure how to create methods, call on TTT game package?
    @Override
    public void submitMove(String playerId, String move) {
        /* TTT move validation */ }

    @Override
    public void endGame(String sessionId) {
        /* TTT cleanup */ }
}
