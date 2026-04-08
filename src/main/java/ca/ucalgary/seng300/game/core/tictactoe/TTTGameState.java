package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.*;

public class TTTGameState implements GameState {

    private static final long serialVersionUID = 1L;

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
        int row = TTTaction.getRowValue();
        int col = TTTaction.getColValue();

        if (board != null && state == State.IN_PROGRESS) {
            Player whoMoved = playerTurn;
            board[row][col] = whoMoved;
            System.out.println("Square "+row+" "+col+" set to"+whoMoved.toString());

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

    /**
     * Rebuilds state from {@link ca.ucalgary.seng300.platform.messages.TTTWireState} (ordinal grid only).
     */
    public static TTTGameState fromWireSnapshot(int[][] cellOrdinals, int playerTurnOrd, int stateOrd) {
        TTTGameState g = new TTTGameState();
        Player[][] b = g.getBoard();
        if (cellOrdinals != null) {
            for (int i = 0; i < 3 && i < cellOrdinals.length; i++) {
                if (cellOrdinals[i] == null) {
                    continue;
                }
                for (int j = 0; j < 3 && j < cellOrdinals[i].length; j++) {
                    b[i][j] = playerFromOrdinal(cellOrdinals[i][j]);
                }
            }
        }
        Player t = playerFromOrdinal(playerTurnOrd);
        g.playerTurn = (t == Player.PLAYER_1 || t == Player.PLAYER_2) ? t : Player.PLAYER_1;
        g.state = stateFromOrdinal(stateOrd);
        return g;
    }

    private static Player playerFromOrdinal(int o) {
        Player[] v = Player.values();
        if (o < 0 || o >= v.length) {
            return Player.EMPTY;
        }
        return v[o];
    }

    private static State stateFromOrdinal(int o) {
        State[] v = State.values();
        if (o < 0 || o >= v.length) {
            return State.IN_PROGRESS;
        }
        return v[o];
    }

    /** Ends the current game in favour of the opponent (online forfeit). */
    public void applyForfeit(Player forfeitingPlayer) {
        if (state != State.IN_PROGRESS) {
            return;
        }
        if (forfeitingPlayer == Player.PLAYER_1) {
            state = State.PLAYER_2_WIN;
        } else if (forfeitingPlayer == Player.PLAYER_2) {
            state = State.PLAYER_1_WIN;
        }
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