package ca.ucalgary.seng300.platform.stubs;

import java.util.concurrent.LinkedBlockingQueue;

// Stub: simulates the TCP network boundary between ClientCore and ServerCore.
// Use Network.createConnectedPair() to get two linked endpoints — messages
// sent from one appear in the other's receive queue, mirroring a real socket pair.
// The real TCP implementation can be swapped in at this boundary.
public class Network {

    private final LinkedBlockingQueue<String> sendQueue;
    private final LinkedBlockingQueue<String> receiveQueue;

    private Network(LinkedBlockingQueue<String> sendQueue, LinkedBlockingQueue<String> receiveQueue) {
        this.sendQueue = sendQueue;
        this.receiveQueue = receiveQueue;
    }

    public void send(String message) {
        if (message == null) return;
        sendQueue.offer(message);
    }

    // Returns null if no message is available (non-blocking).
    public String receive() {
        return receiveQueue.poll();
    }

    // Returns a pair [clientSide, serverSide] whose queues are cross-wired:
    // clientSide.send() -> serverSide.receive(), and vice versa.
    public static Network[] createConnectedPair() {
        LinkedBlockingQueue<String> clientToServer = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<String> serverToClient = new LinkedBlockingQueue<>();
        return new Network[]{
            new Network(clientToServer, serverToClient),
            new Network(serverToClient, clientToServer)
        };
    }
}
