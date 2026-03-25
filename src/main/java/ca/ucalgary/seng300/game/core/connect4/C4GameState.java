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
        int row = C4action.getRowValue();
        int col = C4action.getColValue();

        if (board != null && state == State.IN_PROGRESS) {
            board[row][col] = playerTurn;
            if (playerTurn == Player.PLAYER_1) {
                playerTurn = Player.PLAYER_2;
            } else {
                playerTurn = Player.PLAYER_1;
            }
            successful = true;
        }

        return successful;
    }

    //variant of apply action that only uses the column
    //and figures out what row the 'piece' lands in on its own.
    //included because there were ideas of doing it this way
    //but currently C4ActionPayload has column and row
    //so having both makes it easier to switch if using only column is chosen
    //remember to add @Override and fix the function name if chosen
    public boolean applyActionColOnly(PlayerAction action) {
        boolean successful = false;
        C4PlayerAction C4action = (C4PlayerAction) action;
        int col = C4action.getColValue();

        if (board != null && state == State.IN_PROGRESS) {
            //check from bottom row upward to find the first empty row in the given column
            for (int i = 5; i >= 0; i--) {
                //check if the current row in the selected column is empty
                if (board[i][col] == Player.EMPTY) {
                    //set the position to be claimed by the current player
                    board[i][col] = playerTurn;
                    //change player turn
                    if (playerTurn == Player.PLAYER_1) {
                        playerTurn = Player.PLAYER_2;
                    } else {
                        playerTurn = Player.PLAYER_1;
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
}
