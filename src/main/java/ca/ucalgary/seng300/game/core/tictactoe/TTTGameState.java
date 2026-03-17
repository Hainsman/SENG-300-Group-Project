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

        //Place corresponding player token
        if (board != null && state == State.IN_PROGRESS) {
            board[row][col] = playerTurn;
            if (playerTurn == Player.PLAYER_1) {
                playerTurn = Player.PLAYER_2;
            } else {
                playerTurn = Player.PLAYER_1;
            }
            successful = true;
        }

        //Check for win/game end
        int row_check;
        int col_check;
        //Check for possible win lines from placed token
        for (int r = -1; r < 2; r++) {
            row_check = row + r;
            if (0 <= row_check && row_check <= 2) {
                for (int c = -1; c < 2; c++) {
                    col_check = col + c;
                    if (0 <= col_check && col_check <= 2 && !(c == 0 && r == 0)) {
                        if (board[row_check][col_check] == board[row][col]) {
                            if (((0 <= row_check + r && row_check + r <= 2) && (0 <= col_check + c && col_check + c <= 2) && (board[row_check + r][col_check + c] == board[row][col]))) {
                                switch (board[row][col]) {
                                    case Player.PLAYER_1: state = State.PLAYER_1_WIN;
                                    break;
                                    case Player.PLAYER_2: state = State.PLAYER_2_WIN;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        //Check for full board
        boolean isBoardFull = false;
        if (state == State.IN_PROGRESS) {
            isBoardFull = true;
            for (Player[] r : board) {
                for (Player p : r) {
                    if (p == Player.EMPTY) {
                        isBoardFull = false;
                        break;
                    }
                }
            }
        }
        if (isBoardFull) {
            state = State.TIE;
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
}