package ca.ucalgary.seng300.platform.messages;
/*
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;

import java.io.Serializable;


 //Network-safe snapshot for TTT: only primitive ints (cell ordinals), never enum arrays on the wire.

public final class TTTWireState implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int[][] cells;
    private final int turnOrd;
    private final int stateOrd;

    public TTTWireState(int[][] cells, int turnOrd, int stateOrd) {
        this.cells = cells;
        this.turnOrd = turnOrd;
        this.stateOrd = stateOrd;
    }

    public static TTTWireState from(TTTGameState s) {
        Player[][] b = s.getBoard();
        int[][] c = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                c[i][j] = b[i][j].ordinal();
            }
        }
        return new TTTWireState(c, s.getPlayerTurn().ordinal(), s.getState().ordinal());
    }

    public TTTGameState toGameState() {
        return TTTGameState.fromWireSnapshot(cells, turnOrd, stateOrd);
    }
}
*/