package ca.ucalgary.seng300;

import ca.ucalgary.seng300.platform.client.ClientCore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ClientCore pre-connection state.
 *
 * ClientCore connects to the server only inside run() — the constructor
 * simply stores the CentralController reference and initialises the rule
 * validator. Passing null for CentralController is safe here because ioMain
 * is never accessed before run() is called.
 *
 * Deeper tests (message routing, game state updates) require a live
 * ObjectStream server and are out of scope until a test-harness socket
 * layer is added.
 */
public class ClientCoreTest {

    @Test
    public void isConnected_returnsFalse_beforeConnect() {
        ClientCore client = new ClientCore(null);
        assertFalse(client.isConnected());
    }

    @Test
    public void getMyNetworkRole_returnsNull_whenNoRoleSet() {
        ClientCore client = new ClientCore(null);
        assertNull(client.getMyNetworkRole());
    }

    @Test
    public void submitMove_returnsFalse_whenNotConnected() {
        ClientCore client = new ClientCore(null);
        assertFalse(client.submitMove(0, 0));
    }

    @Test
    public void getLastUpdate_returnsNull_initially() {
        ClientCore client = new ClientCore(null);
        assertNull(client.getLastUpdate());
    }
}