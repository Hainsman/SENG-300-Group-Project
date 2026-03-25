package ca.ucalgary.seng300.platform.messages;

import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Line-based wire format: one JSON line per message.
 * First message from client can be CreateOrJoinSession; server replies with SessionCreated.
 * Then client sends SubmitMove (JSON); server replies with GameStateUpdate (JSON).
 */
public final class WireFormat {
//    private static final Gson GSON = new GsonBuilder().create();
//
//    public static String toJson(Object o) {
//        return GSON.toJson(o);
//    }
//
//    public static SubmitMove fromJsonSubmitMove(String json) {
//        return GSON.fromJson(json, SubmitMove.class);
//    }
//
//    public static GameStateUpdate fromJsonGameStateUpdate(String json) {
//        return GSON.fromJson(json, GameStateUpdate.class);
//    }
//
//    public static CreateOrJoinSession fromJsonCreateOrJoin(String json) {
//        return GSON.fromJson(json, CreateOrJoinSession.class);
//    }
//
//    public static SessionCreated fromJsonSessionCreated(String json) {
//        return GSON.fromJson(json, SessionCreated.class);
//    }

//    public static SessionCreated toSessionCreated(String sessionId, String yourPlayer) {
//        return new SessionCreated(sessionId, yourPlayer);
//    }
//
//    public static GameStateUpdate fromGameState(GameState gs, String sessionId) {
//        Player[][] b = gs.getBoard();
//        String[][] board = new String[3][3];
//        for (int i = 0; i < 3; i++)
//            for (int j = 0; j < 3; j++)
//                board[i][j] = b[i][j].name();
//        String currentTurn = gs.getPlayerTurn().name();
//        String status = gs.getState().name();
//        return new GameStateUpdate(sessionId, board, currentTurn, status);
//    }

    private WireFormat() {}
}
