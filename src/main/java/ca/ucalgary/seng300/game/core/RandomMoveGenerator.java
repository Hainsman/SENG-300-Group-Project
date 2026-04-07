package ca.ucalgary.seng300.game.core;

import ca.ucalgary.seng300.game.core.connect4.ConnectFourRandomMoveGenerator;
import ca.ucalgary.seng300.game.core.tictactoe.TicTacToeRandomMoveGenerator;

public class RandomMoveGenerator {

    // idk why there are two separate classes for this but whatever
    public static int[] pickRandomLegalMove(Player[][] board, GameType gameType) {
        return switch (gameType) {
            case CONNECT4 -> new int[] {0, ConnectFourRandomMoveGenerator.pickRandomLegalColumn(board)};
            case TIC_TAC_TOE -> TicTacToeRandomMoveGenerator.pickRandomEmptySlot(board);
            default -> null;
        };
    }
}
