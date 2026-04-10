package ca.ucalgary.seng300.platform.server;

import ca.ucalgary.seng300.platform.messages.CreateOrJoinSession;
import ca.ucalgary.seng300.platform.messages.SubmitMove;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGame;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServerCoreTest {

    /**
     * Testable subclass so protected methods can be accessed.
     */
    static class TestableServerCore extends ServerCore {
        public TestableServerCore(int port) {
            super(port);
        }

        public String callHandleLine(String line) {
            return handleLine(line);
        }

        public String callHandleCreateOrJoin(CreateOrJoinSession req) {
            return handleCreateOrJoin(req);
        }

        public String callHandleSubmitMove(SubmitMove move) {
            return handleSubmitMove(move);
        }
    }

    @Test
    void handleSubmitMoveAppliesMoveToGameState() throws Exception {
        TestableServerCore server = new TestableServerCore(2020);
        TTTGame game = new TTTGame();
        setField(server, "game", game);

        String result = server.callHandleSubmitMove(new SubmitMove("MOVE", "P1", 0, 0));
        TTTGameState state = (TTTGameState) game.getGameState();

        assertNull(result);
        assertEquals(Player.PLAYER_1, state.getBoard()[0][0]);
    }

    @Test
    void handleLineMoveMutatesGameStateAndAdvancesTurn() throws Exception {
        TestableServerCore server = new TestableServerCore(2020);
        TTTGame game = new TTTGame();
        setField(server, "game", game);

        server.callHandleLine("MOVE P1 session-1 1 1");
        TTTGameState state = (TTTGameState) game.getGameState();

        assertEquals(Player.PLAYER_1, state.getBoard()[1][1]);
        assertEquals(Player.PLAYER_2, state.getPlayerTurn());
    }

    @Test
    void handleLineBadRequestWritesErrorToClient() throws Exception {
        TestableServerCore server = new TestableServerCore(2020);
        StringWriter sink = new StringWriter();
        PrintWriter out = new PrintWriter(sink, true);
        ClientHandler handler = new ClientHandler();
        handler.out = out;
        ArrayList<ClientHandler> clients = new ArrayList<>();
        clients.add(handler);
        setField(server, "clients", clients);

        String result = server.callHandleLine("NOT_A_REAL_COMMAND");

        assertNull(result);
        assertTrue(sink.toString().contains("ERROR: bad request."));
    }

    @Test
    void handleCreateSessionAddsSessionRecord() throws Exception {
        TestableServerCore server = new TestableServerCore(2020);
        StringWriter sink = new StringWriter();
        PrintWriter out = new PrintWriter(sink, true);
        ClientHandler handler = new ClientHandler();
        handler.out = out;
        ArrayList<ClientHandler> clients = new ArrayList<>();
        clients.add(handler);
        setField(server, "clients", clients);

        String result = server.callHandleCreateOrJoin(new CreateOrJoinSession("CREATE_SESSION", "?", "P1", "TTT"));
        Map<?, ?> sessions = (Map<?, ?>) getField(server, "sessions");

        assertNull(result);
        assertEquals(1, sessions.size());
    }

    @Test
    void constructorStoresPort() throws Exception {
        TestableServerCore server = new TestableServerCore(2020);
        assertEquals(2020, getPort(server));
    }

    private boolean getRunning(ServerCore server) throws Exception {
        Field field = ServerCore.class.getDeclaredField("running");
        field.setAccessible(true);
        return field.getBoolean(server);
    }

    private int getPort(ServerCore server) throws Exception {
        Field field = ServerCore.class.getDeclaredField("port");
        field.setAccessible(true);
        return field.getInt(server);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getSuperclass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private Object getField(Object target, String name) throws Exception {
        Field field = target.getClass().getSuperclass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }
}
