package ca.ucalgary.seng300.platform.lobby;

import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.domain.PlayerStats;

import java.util.List;

public class C4Lobby extends Lobby  {
    @Override
    public int getGameType() { return Game.CONNECT_FOUR; }

    @Override
    public void startGame(List<PlayerStats> players) {
        /* ConnectFour specific setup */ }

    @Override
    public void submitMove(String playerId, String move) {
        /* ConnectFour move validation */ }

    @Override
    public void endGame(String sessionId) {
        /* ConnectFour cleanup */ }
}
