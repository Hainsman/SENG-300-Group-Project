package ca.ucalgary.seng300.platform.session;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;

public interface Session {
    boolean submitMove(int row, int col);
    Player[][] getBoard();
    Player getTurn();
    State getState();
    boolean isGameOver();
}
