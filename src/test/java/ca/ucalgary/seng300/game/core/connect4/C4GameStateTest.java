package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.tictactoe.TTTActionType;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class C4GameStateTest {
    private C4GameState gameState;

    @BeforeEach
    void setUp() {
        gameState = new C4GameState();
    }

    //Action succeeds and places token correctly. Row entry on C4PlayerAction is currently redundant
    @Test
    void applyActionUpdatesBoard(){
        C4PlayerAction action = new C4PlayerAction("player1", C4ActionType.MOVE.toString(),1, 1);

        gameState.applyAction(action);

        assertSame(Player.PLAYER_1, gameState.getBoard()[5][1]);
    }
}
