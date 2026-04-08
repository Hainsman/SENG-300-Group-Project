package ca.ucalgary.seng300.platform.messages;
/*
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.connect4.C4GameState;

import java.io.Serializable;


 //Network-safe snapshot for Connect Four: primitive cell ordinals only.

public final class C4WireState implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int[][] cells;
    private final int turnOrd;
    private final int stateOrd;

    public C4WireState(int[][] cells, int turnOrd, int stateOrd) {
        this.cells = cells;
        this.turnOrd = turnOrd;
        this.stateOrd = stateOrd;
    }

    public static C4WireState from(C4GameState s) {
        Player[][] b = s.getBoard();
        int[][] c = new int[6][7];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                c[i][j] = b[i][j].ordinal();
            }
        }
        return new C4WireState(c, s.getPlayerTurn().ordinal(), s.getState().ordinal());
    }

    public C4GameState toGameState() {
        return C4GameState.fromWireSnapshot(cells, turnOrd, stateOrd);
    }
}
*/