package ca.ucalgary.seng300.game.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomMoveGenerator {

    // idk why there are two separate classes for this but whatever
    public static int[] pickRandomLegalMove(Player[][] board, GameType gameType) {
        return switch (gameType) {
            case CONNECT4 -> new int[] {0, pickRandomLegalColumn(board)};
            case TIC_TAC_TOE -> pickRandomEmptySlot(board);
            default -> null;
        };
    }

    /**
     * Picks a legal random empty cell for Tic-Tac-Toe. Simple stand-in for a second player (no AI).
     */

    public static int[] pickRandomEmptySlot(Player[][] board) {
        if (board == null || board.length != 3) {
            return null;
        }
        List<int[]> empties = new ArrayList<>();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c] == Player.EMPTY) {
                    empties.add(new int[] { r, c });
                }
            }
        }
        if (empties.isEmpty()) {
            return null;
        }
        int i = ThreadLocalRandom.current().nextInt(empties.size());
        return empties.get(i);
    }

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
