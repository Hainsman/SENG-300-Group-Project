package ca.ucalgary.seng300;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.game.core.Player;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class ClientCoreTest {

    @Test
    void isConnectedIsFalseBeforeSocketSetup() {
        ClientCore client = new ClientCore(null);
        assertFalse(client.isConnected());
    }

    @Test
    void getMyNetworkRoleMapsP1AndP2() throws Exception {
        ClientCore client = new ClientCore(null);
        setPrivateField(client, "myPlayerId", "P1");
        assertEquals(Player.PLAYER_1, client.getMyNetworkRole());

        setPrivateField(client, "myPlayerId", "P2");
        assertEquals(Player.PLAYER_2, client.getMyNetworkRole());
    }

    @Test
    void getMyNetworkRoleReturnsNullWhenUnknown() throws Exception {
        ClientCore client = new ClientCore(null);
        setPrivateField(client, "myPlayerId", "UNKNOWN");
        assertNull(client.getMyNetworkRole());
    }

    @Test
    void disconnectedSendOperationsAreNoOps() {
        ClientCore client = new ClientCore(null);
        assertDoesNotThrow(() -> client.msgSend("hello"));
        assertDoesNotThrow(client::sendLogout);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}