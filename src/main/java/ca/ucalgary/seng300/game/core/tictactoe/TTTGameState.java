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