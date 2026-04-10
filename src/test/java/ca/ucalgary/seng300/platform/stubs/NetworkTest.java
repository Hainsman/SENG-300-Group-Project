package ca.ucalgary.seng300.platform.stubs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {

    private Network clientSide;
    private Network serverSide;

    @BeforeEach
    void setUp() {
        Network[] pair = Network.createConnectedPair();
        clientSide = pair[0];
        serverSide = pair[1];
    }

    // --- send / receive ---

    @Test
    void clientSend_messageIsReceivedByServer() {
        clientSide.send("hello");
        assertEquals("hello", serverSide.receive());
    }

    @Test
    void serverSend_messageIsReceivedByClient() {
        serverSide.send("hello back");
        assertEquals("hello back", clientSide.receive());
    }

    @Test
    void receive_withNoMessagesPending_returnsNull() {
        assertNull(clientSide.receive());
    }

    // --- null and empty messages ---

    @Test
    void send_nullMessage_isNotQueued() {
        clientSide.send(null);
        assertNull(serverSide.receive());
    }

    @Test
    void send_emptyString_isDelivered() {
        clientSide.send("");
        assertEquals("", serverSide.receive());
    }

    // --- ordering ---

    @Test
    void multipleMessages_areReceivedInFIFOOrder() {
        clientSide.send("first");
        clientSide.send("second");
        clientSide.send("third");

        assertEquals("first", serverSide.receive());
        assertEquals("second", serverSide.receive());
        assertEquals("third", serverSide.receive());
    }

    // --- bidirectionality ---

    @Test
    void bidirectional_bothSidesCanSendAndReceiveSimultaneously() {
        clientSide.send("from client");
        serverSide.send("from server");

        assertEquals("from client", serverSide.receive());
        assertEquals("from server", clientSide.receive());
    }

    @Test
    void clientMessages_doNotAppearInClientReceive() {
        clientSide.send("client message");
        // client should not receive its own outbound message
        assertNull(clientSide.receive());
    }

    @Test
    void serverMessages_doNotAppearInServerReceive() {
        serverSide.send("server message");
        // server should not receive its own outbound message
        assertNull(serverSide.receive());
    }

    // --- multiple independent pairs ---

    @Test
    void createConnectedPair_twoPairs_areIsolatedFromEachOther() {
        Network[] pair2 = Network.createConnectedPair();
        clientSide.send("pair1 message");

        // pair2 should be unaffected
        assertNull(pair2[1].receive());
    }
}
