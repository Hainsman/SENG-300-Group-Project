package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TTTRuleValidatorTest {

    private TTTRuleValidator validator;
    private TTTGameState gameState;

    @BeforeEach
    void setUp() {
        validator = new TTTRuleValidator();
        gameState = new TTTGameState();
        validator.setPlayerIdToPlayer(Map.ofEntries(Map.entry("player1", Player.PLAYER_1),  Map.entry("player2", Player.PLAYER_2)));
    }

    @Test
    // Tests inserting a piece at the center of an empty board
    void validateMoveReturnsTrueOnValidMove() {
        TTTPlayerAction action = new TTTPlayerAction("player1", TTTActionType.MOVE.toString(), 1, 1);

        boolean isValid = validator.validateMove(gameState, action);

        assertTrue(isValid, "Move should be valid on an empty board.");
    }

    @Test
    // Detects when a piece is inserted outside the bounds of the board
    void isOutOfBounds() {
        TTTPlayerAction action = new TTTPlayerAction("player1", TTTActionType.MOVE.toString(), 3, 3);

        boolean isValid = validator.validateMove(gameState, action);

        assertFalse(isValid, "Inserting piece outside board should return False.");
    }

    @Test
    // Returns False if a player attempts to insert a piece over another
    void isEmptyCell() {
        // simulate a board with a piece already on it
        gameState.getBoard()[0][0] = Player.PLAYER_1;

        TTTPlayerAction action = new TTTPlayerAction("player2", TTTActionType.MOVE.toString(), 0, 0);

        boolean isValid = validator.validateMove(gameState, action);

        assertFalse(isValid, "Inserting a piece on top of another player's piece is an invalid move.");

    }

    @Test
    // Tests if player can make a move during another player's turn
    void playerMakesMoveDuringOpponentsTurn() {
        // Make it player2's turn
        gameState.playerTurn = Player.PLAYER_2;

        TTTPlayerAction action = new TTTPlayerAction("player1", TTTActionType.MOVE.toString(), 0, 0);

        boolean isValid = validator.validateMove(gameState, action);

        assertFalse(isValid, "Not the player's turn");
    }

    //Todo: Fix TTTRuleValidator - player1 making a move during player2's turn is returning true

    @Test
    // Tests if player can make a move during another player's turn
    void playerMakesMoveWhileGameNotInProgress() {
        gameState.state = State.TIE;

        TTTPlayerAction action = new TTTPlayerAction("player1", TTTActionType.MOVE.toString(), 0, 0);

        boolean isValid = validator.validateMove(gameState, action);

        assertFalse(isValid, "Cannot make a move after game has ended");
    }

}
