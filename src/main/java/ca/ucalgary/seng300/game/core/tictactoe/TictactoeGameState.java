package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.*;

import java.util.ArrayList;

public class TictactoeGameState implements GameState {

    ArrayList<Player> board = null;

    Player[][] board1 = null;

    Player playerTurn = Player.PLAYER_1;
    State state = State.IN_PROGRESS;

    //TictactoePlayerAction aaa = new TictactoePlayerAction("aa", TictactoeActionType.MOVE, new TictactoeActionPayload(1, 2));

    @Override
    public boolean applyAction(PlayerAction action) {
        boolean successful = false;
        TictactoePlayerAction TTTaction = (TictactoePlayerAction) action;
        //TTTaction.getActionPayload().row();
        int row = TTTaction.getActionPayload().row();
        int col = TTTaction.getActionPayload().col();

        //if board is not null then we can update it
        if (board1 != null) {
            board1[row][col] = playerTurn;
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
    public ArrayList<Player> getBoard() {

        //applyAction(aaa);

        return null;
    }



    public Player[][] getBoard1() {
        if (this.board1 == null) {
            //create the board in an empty state
            this.board1 = new Player[2][2];
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= 2; j++) {
                    this.board1[i][j] = Player.EMPTY;
                }
            }
        }
        //by this point the board should exist, so return it
        return this.board1;
    }

}
