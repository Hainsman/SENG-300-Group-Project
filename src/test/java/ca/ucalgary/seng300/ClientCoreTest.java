package ca.ucalgary.seng300;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerMove;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class ClientCoreTest {

    @Test
    void submitMoveReturnsFalseWhenDisconnected() {
        ClientCore client = new ClientCore(null);
        assertFalse(client.submitMove(0, 0));
    }

    @Test
    void submitMoveWritesMoveMessageWhenConnected() throws Exception {
        ClientCore client = new ClientCore(null);
        ByteArrayOutputStream writeBuffer = new ByteArrayOutputStream();
        ObjectOutputStream sender = new ObjectOutputStream(writeBuffer);
        ObjectInputStream reader = new ObjectInputStream(new ByteArrayInputStream(serializedObject("noop")));
        setField(client, "servSend", sender);
        setField(client, "servRead", reader);
        client.CID = "cid-7";

        boolean ok = client.submitMove(1, 2);
        sender.flush();

        assertTrue(ok);
        ObjectInputStream captured = new ObjectInputStream(new ByteArrayInputStream(writeBuffer.toByteArray()));
        TTTPlayerMove sent = (TTTPlayerMove) captured.readObject();
        assertEquals("cid-7", sent.getPlayerID());
        assertEquals("MOVE", sent.getActionType());
        assertEquals(1, sent.getRow());
        assertEquals(2, sent.getCol());
    }

    @Test
    void loginSendsLoginMessageAndParsesSuccessPayload() throws Exception {
        ClientCore client = new ClientCore(null);
        ByteArrayOutputStream writeBuffer = new ByteArrayOutputStream();
        ObjectOutputStream sender = new ObjectOutputStream(writeBuffer);
        ObjectInputStream reader = new ObjectInputStream(new ByteArrayInputStream(serializedObject("noop")));
        setField(client, "servSend", sender);
        setField(client, "servRead", reader);

        Object loginLock = getField(client, "loginLock");
        Thread notifier = new Thread(() -> {
            try {
                Thread.sleep(100);
                synchronized (loginLock) {
                    setField(client, "loginResult", "SUCCESS|token123|alice|42");
                    loginLock.notify();
                }
            } catch (Exception ignored) {
            }
        });
        notifier.start();

        boolean ok = client.login("alice", "pw");
        notifier.join();
        sender.flush();

        assertTrue(ok);
        assertEquals(42, client.getUserId());

        ObjectInputStream captured = new ObjectInputStream(new ByteArrayInputStream(writeBuffer.toByteArray()));
        sysMSG sent = (sysMSG) captured.readObject();
        assertEquals("LOGIN", sent.getType());
        assertEquals("alice", sent.getFieldA());
        assertEquals("pw", sent.getFieldB());
    }

    @Test
    void networkRoleMapsToPlayerEnum() throws Exception {
        ClientCore client = new ClientCore(null);
        setField(client, "myPlayerId", "P1");
        assertEquals(Player.PLAYER_1, client.getMyNetworkRole());
        setField(client, "myPlayerId", "P2");
        assertEquals(Player.PLAYER_2, client.getMyNetworkRole());
    }

    private static byte[] serializedObject(Object o) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(o);
        out.flush();
        return bytes.toByteArray();
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static Object getField(Object target, String fieldName) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(target);
    }
}