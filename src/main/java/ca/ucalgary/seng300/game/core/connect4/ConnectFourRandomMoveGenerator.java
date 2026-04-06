package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Picks a legal random column (non-full) for Connect Four. Simple stand-in for a second player (no AI).
 */
public final class ConnectFourRandomMoveGenerator {

    private ConnectFourRandomMoveGenerator() {}

    /**
     * @return column index {@code 0..6} where a piece can drop, or {@code null} if all columns are full
     */
    public static Integer pickRandomLegalColumn(Player[][] board) {
        if (board == null || board.length != 6 || board[0] == null || board[0].length != 7) {
            return null;
        }
        List<Integer> open = new ArrayList<>();
        for (int c = 0; c < 7; c++) {
            if (board[0][c] == Player.EMPTY) {
                open.add(c);
            }
        }
        if (open.isEmpty()) {
            return null;
        }
        int i = ThreadLocalRandom.current().nextInt(open.size());
        return open.get(i);
    }
}
