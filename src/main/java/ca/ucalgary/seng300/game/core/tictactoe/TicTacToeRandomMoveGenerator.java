package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Picks a legal random empty cell for Tic-Tac-Toe. Simple stand-in for a second player (no AI).
 */
public final class TicTacToeRandomMoveGenerator {

    private TicTacToeRandomMoveGenerator() {}

    /**
     * @return {@code new int[]{row, col}} for a random empty cell, or {@code null} if the board is full
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
}
