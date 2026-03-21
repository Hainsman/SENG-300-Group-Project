package ca.ucalgary.seng300.platform.server;

import ca.ucalgary.seng300.platform.messages.CreateOrJoinSession;
import ca.ucalgary.seng300.platform.messages.SubmitMove;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

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
    public void testStartSetsRunningTrue() throws Exception {
        TestableServerCore server = new TestableServerCore(2020);

        server.start();

        assertTrue(getRunning(server));
    }

    @Test
    public void testStopSetsRunningFalse() throws Exception {
        TestableServerCore server = new TestableServerCore(2020);

        server.start();
        server.stop();

        assertFalse(getRunning(server));
    }

    @Test
    public void testHandleLineReturnsNullForStub() {
        TestableServerCore server = new TestableServerCore(2020);

        String result = server.callHandleLine("any input");

        assertNull(result);
    }

    @Test
    public void testHandleCreateOrJoinReturnsNullForStub() {
        TestableServerCore server = new TestableServerCore(2020);

        String result = server.callHandleCreateOrJoin(null);

        assertNull(result);
    }

    @Test
    public void testHandleSubmitMoveReturnsNullForStub() {
        TestableServerCore server = new TestableServerCore(2020);

        String result = server.callHandleSubmitMove(null);

        assertNull(result);
    }

    @Test
    public void testConstructorStoresPort() throws Exception {
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
}
