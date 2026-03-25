package ca.ucalgary.seng300.platform.messages;

import ca.ucalgary.seng300.game.core.Player;

import java.util.UUID;

/**
 * Server -> client: response after create or join session.
 * yourPlayer = "PLAYER_1" | "PLAYER_2"
 */
public class SessionCreated extends Message {
    private Player yourPlayer; // "PLAYER_1" or "PLAYER_2"
    private String message;    // optional error message

    public SessionCreated(UUID globallyUniqueSessionID, Player yourPlayer, String message) {
        super(globallyUniqueSessionID, SessionCreated.class.getSimpleName());
        this.yourPlayer = yourPlayer;
        this.message = message;
    }

    public Player getYourPlayer() { return yourPlayer; }
    public void setYourPlayer(Player yourPlayer) { this.yourPlayer = yourPlayer; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
