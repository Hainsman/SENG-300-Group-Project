package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.*;

public class C4GameState implements GameState {
    private Player[][] board = {
            {Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY},
            {Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY},
            {Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY},
            {Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY},
            {Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY},
            {Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY, Player.EMPTY}
    };
    Player playerTurn = Player.PLAYER_1;
    State state = State.IN_PROGRESS;

    @Override
    public boolean applyAction(PlayerAction action) {
        boolean successful = false;
        C4PlayerAction C4action = (C4PlayerAction) action;
        int col = C4action.getColValue();
        if (board != null && state == State.IN_PROGRESS) {
            Player whoMoved = playerTurn;
            //check from bottom row upward to find the first empty row in the given column
            for (int i = 5; i >= 0; i--) {
                //check if the current row in the selected column is empty
                if (board[i][col] == Player.EMPTY) {
                    //set the position to be claimed by the current player
                    board[i][col] = playerTurn;
                    //check if game is over
                    if (checkFourInARow(i, col, whoMoved)) {
                        state = (whoMoved == Player.PLAYER_1) ? State.PLAYER_1_WIN : State.PLAYER_2_WIN;
                    } else if (isBoardFull()) {
                        state = State.TIE;
                    } else { //game is not over so change turn
                        if (playerTurn == Player.PLAYER_1) {
                            playerTurn = Player.PLAYER_2;
                        } else {
                            playerTurn = Player.PLAYER_1;
                        }
                    }

                    successful = true;
                    break; //Added to fix the issue where it was filling the entire column with alternating players
                }
            }
        }
        return successful;
    }

    @Override
    public Player[][] getBoard() {
        if (this.board == null) {
            this.board = new Player[6][7];
            for (int i = 0; i < 6; i++)
                for (int j = 0; j < 7; j++) {
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

    private boolean checkFourInARow(int row, int col, Player player) {
        int consecutivePieces = 0;
        //check if win is in row
        //works across entire row checking if the given player has 4 consecutively
        for (int i = 0; i < 7; i++) {
            if (board[row][i] == player) {
                consecutivePieces++;
            } else {
                consecutivePieces = 0;
            }
            if (consecutivePieces >= 4) {
                return true;
            }
        }
        //check if win is in column
        //works across entire column checking if the given player has 4 consecutively
        consecutivePieces = 0;
        for (int i = 0; i < 6; i++) {
            if (board[i][col] == player) {
                consecutivePieces++;
            } else {
                consecutivePieces = 0;
            }
            if (consecutivePieces >= 4) {
                return true;
            }
        }
        //check if win is in forward slash
        consecutivePieces = 0;
        int diagRow;
        int diagCol;
        //getting the lower leftmost possible part of a forward slash win that crosses the given point
        if (col > (5 - row)) {
            diagRow = 5;
            diagCol = col - (5 - row);
        } else {
            diagRow = row + col;
            diagCol = 0;
        }
        while (diagRow >= 0 && diagCol < 7) {
            if (board[diagRow][diagCol] == player) {
                consecutivePieces++;
            } else {
                consecutivePieces = 0;
            }
            if (consecutivePieces >= 4) {
                return true;
            }
            diagRow--;
            diagCol++;
        }

        //check if win is in backward slash
        consecutivePieces = 0;
        //getting the upper leftmost possible part of a backward slash win that crosses the given point
        if (col > row) {
            diagRow = 0;
            diagCol = col - row;
        } else {
            diagRow = row - col;
            diagCol = 0;
        }
        while (diagRow < 6 && diagCol < 7) {
            if (board[diagRow][diagCol] == player) {
                consecutivePieces++;
            } else {
                consecutivePieces = 0;
            }
            if (consecutivePieces >= 4) {
                return true;
            }
            diagRow++;
            diagCol++;
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (board[i][j] == Player.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
}
