package ca.ucalgary.seng300.game.core;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TestClientGame implements ClientGame {

    private GameState state = new TestGameState();

    @Override
    public boolean validate(PlayerAction action) {
        return true;
    }

    @Override
    public List<PlayerAction> getValidActions() {
        return List.of(new TestAction("p1", "MOVE", 0, 0));
    }

    @Override
    public void updateLocalState(GameState gameState) {
        this.state = gameState;
    }

    @Override
    public GameState getLocalState() {
        return state;
    }
}

public class ClientGameTest {

    @Test
    void testValidateAlwaysTrue() {
        ClientGame game = new TestClientGame();
        assertTrue(game.validate(new TestAction("p1", "MOVE", 0, 0)));
    }

    @Test
    void testUpdateLocalState() {
        ClientGame game = new TestClientGame();
        GameState newState = new TestGameState();

        game.updateLocalState(newState);

        assertEquals(newState, game.getLocalState());
    }
}