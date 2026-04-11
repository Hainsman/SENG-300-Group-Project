package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Connect4RandomMoveGenerator {
    private static final Random RNG = new Random();

    public static Integer chooseColumn(C4GameState state) {
        if (state == null || state.getState() != ca.ucalgary.seng300.game.core.State.IN_PROGRESS) {
            return null;
        }
        Player[][] board = state.getBoard();
        List<Integer> legalColumns = new ArrayList<>();
        for (int col = 0; col < board[0].length; col++) {
            if (board[0][col] == Player.EMPTY) {
                legalColumns.add(col);
            }
        }
        if (legalColumns.isEmpty()) {
            return null;
        }
        return legalColumns.get(RNG.nextInt(legalColumns.size()));
    }
}
