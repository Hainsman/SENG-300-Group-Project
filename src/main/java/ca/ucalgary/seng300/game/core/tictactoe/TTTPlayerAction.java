package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.PlayerAction;

public class TTTPlayerAction extends PlayerAction {
    public TTTPlayerAction(Player player, String actionType, int row, int col) {
        super(player, actionType, row, col);
    }

    @Override
    public int getRowValue() {
        return super.getRowValue();
    }

    @Override
    public int getColValue() {
        return super.getColValue();
    }


    @Override
    public String getActionType() {
        return super.getActionType();
    }
}
