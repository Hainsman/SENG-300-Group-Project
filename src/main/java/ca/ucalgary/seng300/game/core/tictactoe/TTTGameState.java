package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.*;

public class TTTGameState implements GameState {

    private Player[][] board = {
        {Player.EMPTY, Player.EMPTY, Player.EMPTY},
        {Player.EMPTY, Player.EMPTY, Player.EMPTY},
        {Player.EMPTY, Player.EMPTY, Player.EMPTY}
    };
    Player playerTurn = Player.PLAYER_1;
    State state = State.IN_PROGRESS;

    @Override
    public boolean applyAction(PlayerAction action) {
        boolean successful = false;
        TTTPlayerAction TTTaction = (TTTPlayerAction) action;
        int row = TTTaction.getActionPayload().row();
        int col = TTTaction.getActionPayload().col();

        if (board != null && state == State.IN_PROGRESS) {
            Player whoMoved = playerTurn;
            board[row][col] = whoMoved;

            // Check if the move wins the game (3 in a row)
            if (checkThreeInARow(row, col, whoMoved)) {
                state = (whoMoved == Player.PLAYER_1) ? State.PLAYER_1_WIN : State.PLAYER_2_WIN;
            } else if (isBoardFull()) {
                state = State.TIE;
            } else {
                if (playerTurn == Player.PLAYER_1) {
                    playerTurn = Player.PLAYER_2;
                } else {
                    playerTurn = Player.PLAYER_1;
                }
            }
            successful = true;
        }

        return successful;
    }

    @Override
    public Player[][] getBoard() {
        if (this.board == null) {
            this.board = new Player[3][3];
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    this.board[i][j] = Player.EMPTY;
                }
        }
        return this.board;
    }

    public Player getPlayerTurn() {
        return this.playerTurn;
    }

    public State getState() {
        return this.state;
    }

    /** Returns true if the given player has three in a row including (row, col). */
    private boolean checkThreeInARow(int row, int col, Player player) {
        // Row
        if (board[row][0] == player && board[row][1] == player && board[row][2] == player) {
            return true;
        }
        // Column
        if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {
            return true;
        }
        // Main diagonal
        if (row == col && board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        // Anti-diagonal
        if (row + col == 2 && board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == Player.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Player[] r : board)
            s.append(java.util.Arrays.toString(r)).append('\n');
        return s.toString();
    }
}