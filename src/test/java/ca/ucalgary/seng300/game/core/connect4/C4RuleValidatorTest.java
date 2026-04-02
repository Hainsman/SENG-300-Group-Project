package ca.ucalgary.seng300.game.core.connect4;
import ca.ucalgary.seng300.game.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class C4RuleValidatorTest {

    private C4RuleValidator validator;
    private C4GameState gameState;

    @BeforeEach
    public void setUp() {
        validator = new C4RuleValidator();
        gameState = new C4GameState();
    }

    @Test
    public void ValidMove_ReturnsTrue() {
        // assume gameState initializes in State.IN_PROGRESS with an empty board
        String currentPlayerTurn = gameState.getPlayerTurn().toString();
        // player attempts to drop a piece in column 3
        C4PlayerAction action = new C4PlayerAction(currentPlayerTurn, "MOVE", 5, 3);
        // should return true since it's a valid move
        assertTrue(validator.validateMove(gameState, action));
    }

    @Test
    public void ActionPlayedOutOfTurnReturnsFalse() {
        String actualTurn = gameState.getPlayerTurn().toString();
        String wrongPlayerID = actualTurn.equals("PLAYER_1") ? "PLAYER_2" : "PLAYER_1";
        C4PlayerAction action = new C4PlayerAction(wrongPlayerID, "MOVE", 5, 3);
        // a move made by the player whose turn it isn't should be rejected
        assertFalse(validator.validateMove(gameState, action));
    }

    @Test
    public void ColumnFullReturnsFalse() {
        String currentPlayerTurn = gameState.getPlayerTurn().toString();
        Player[][] board = gameState.getBoard();

        // manually fill column 0 to the very top row (row 0)
        for (int row = 0; row < 6; row++) {
            board[row][0] = Player.PLAYER_1;
        }

        C4PlayerAction action = new C4PlayerAction(currentPlayerTurn, "MOVE", 0, 0);
        assertFalse(validator.validateMove(gameState, action));
    }

    // testing GetValidActions
    @Test
    public void EmptyBoardReturnsBottomRowForAllColumns() {
        List<PlayerAction> actions = validator.getValidActions(gameState);

        // An empty board should have exactly 7 valid moves
        assertEquals(7, actions.size());

        for (PlayerAction action : actions) {
            C4PlayerAction c4Action = (C4PlayerAction) action;
            assertEquals(5, c4Action.getRowValue());
            assertEquals("MOVE", c4Action.getActionType());
        }
    }

    @Test
    public void PartiallyFilledBoardCalculatesCorrectRows() {
        Player[][] board = gameState.getBoard();

        // Fill the bottom two slots of column 2
        board[5][2] = Player.PLAYER_1;
        board[4][2] = Player.PLAYER_2;

        // Completely fill column 4
        for (int row = 0; row < 6; row++) {
            board[row][4] = Player.PLAYER_1;
        }

        List<PlayerAction> actions = validator.getValidActions(gameState);

        // 7 total columns - 1 full column = 6 valid moves remaining
        // A completely full column should not generate a valid move
        assertEquals(6, actions.size());

        boolean foundCol2Move = false;
        for (PlayerAction action : actions) {
            C4PlayerAction c4Action = (C4PlayerAction) action;
            if (c4Action.getColValue() == 2) {
                // the lowest empty row for column 2 should now be index 3
                assertEquals(3, c4Action.getRowValue());
                foundCol2Move = true;
            }
        }
        // A valid move for column 2 must exist
        assertTrue(foundCol2Move);
    }
}
