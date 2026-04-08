package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class TicTacToeRandomMoveGenerator {
    private static final Random RNG = new Random();

    private TicTacToeRandomMoveGenerator() {}

    public static int[] chooseMove(TTTGameState state) {
        if (state == null || state.getState() != ca.ucalgary.seng300.game.core.State.IN_PROGRESS) {
            return null;
        }
        Player[][] board = state.getBoard();
        List<int[]> legalMoves = new ArrayList<>();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == Player.EMPTY) {
                    legalMoves.add(new int[] { row, col });
                }
            }
        }
        if (legalMoves.isEmpty()) {
            return null;
        }
        return legalMoves.get(RNG.nextInt(legalMoves.size()));
    }
}
