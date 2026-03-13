package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.*;

public class TTTGameState implements GameState {

    private Player[][] board = null;
    Player playerTurn = Player.PLAYER_1;
    State state = State.IN_PROGRESS;


    @Override
    public boolean applyAction(PlayerAction action) {
        boolean successful = false;
        TTTPlayerAction TTTaction = (TTTPlayerAction) action;
        int row = TTTaction.getActionPayload().row();
        int col = TTTaction.getActionPayload().col();

        //if board is not null then we can update it, but moves should only happen if the state is "IN_PROGRESS"
        if (board != null && state == State.IN_PROGRESS) {
            board[row][col] = playerTurn;
            //switch the turn
            if (playerTurn == Player.PLAYER_1) {
                playerTurn = Player.PLAYER_2;
            } else {
                playerTurn = Player.PLAYER_1;
            }
            successful = true;
        }

        return successful;
    }

    @Override
    public Player[][] getBoard() {
        if (this.board == null) {
            //create the board in an empty state
            this.board = new Player[2][2];
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= 2; j++) {
                    this.board[i][j] = Player.EMPTY;
                }
            }
        }
        //by this point the board should exist, so return it
        return this.board;
    }
}
