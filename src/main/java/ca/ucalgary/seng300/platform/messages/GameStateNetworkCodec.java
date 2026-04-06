package ca.ucalgary.seng300.platform.messages;

import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.connect4.C4GameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;

import java.io.Serializable;

/**
 * Encodes live {@link GameState} instances for {@link java.io.ObjectOutputStream} so clients never
 * deserialize fragile enum 2D arrays over the socket.
 */
public final class GameStateNetworkCodec {

    private GameStateNetworkCodec() {}

    public static Serializable encode(GameState gs) {
        if (gs instanceof TTTGameState t) {
            return TTTWireState.from(t);
        }
        if (gs instanceof C4GameState c) {
            return C4WireState.from(c);
        }
        throw new IllegalArgumentException("Unsupported game state type: " + (gs != null ? gs.getClass() : "null"));
    }
}
