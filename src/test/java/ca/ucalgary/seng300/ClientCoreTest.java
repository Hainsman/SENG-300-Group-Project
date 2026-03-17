package ca.ucalgary.seng300;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClientCoreTest {

    private final InputStream originalSystemIn = System.in;

    @AfterEach
    public void restoreSystemIn() {
        System.setIn(originalSystemIn);
    }

    @Test
    public void testClientConnectsAndSendsExit() throws Exception {
        try (FakeServer server = new FakeServer("1")) {
            server.start();

            System.setIn(new ByteArrayInputStream("exit\n".getBytes(StandardCharsets.UTF_8)));

            runClient(server.getPort());

            List<String> received = server.getReceivedMessages();
            assertTrue(received.size() >= 2);
            assertEquals("This is Client 1", received.get(0));
            assertEquals("exit", received.get(1));
        }
    }

    @Test
    public void testRlCommandSendsExpectedMessages() throws Exception {
        try (FakeServer server = new FakeServer("2")) {
            server.start();

            System.setIn(new ByteArrayInputStream("rl\nexit\n".getBytes(StandardCharsets.UTF_8)));

            runClient(server.getPort());

            List<String> received = server.getReceivedMessages();
            assertTrue(received.contains("This is Client 2"));
            assertTrue(received.contains("rl"));
            assertTrue(received.contains("AUTH#Mjolnir Recon"));
            assertTrue(received.contains("CHAT#JOIN#2#Mjolnir Recon"));
            assertTrue(received.contains("exit"));
        }
    }

    @Test
    public void testDrCommandSendsExpectedMessages() throws Exception {
        try (FakeServer server = new FakeServer("3")) {
            server.start();

            System.setIn(new ByteArrayInputStream("dr\nexit\n".getBytes(StandardCharsets.UTF_8)));

            runClient(server.getPort());

            List<String> received = server.getReceivedMessages();
            assertTrue(received.contains("This is Client 3"));
            assertTrue(received.contains("dr"));
            assertTrue(received.contains("AUTH#Durandal"));
            assertTrue(received.contains("CHAT#JOIN#3#Durandal"));
            assertTrue(received.contains("exit"));
        }
    }

    private void runClient(int port) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<?> future = executor.submit(() -> {
                new ClientCore("localhost", port);
            });

            future.get(8, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    private static class FakeServer implements AutoCloseable {
        private final String clientIdToSend;
        private final List<String> receivedMessages = new CopyOnWriteArrayList<>();

        private ServerSocket serverSocket;
        private ExecutorService executor;

        FakeServer(String clientIdToSend) {
            this.clientIdToSend = clientIdToSend;
        }

        void start() throws IOException {
            serverSocket = new ServerSocket(0); // random free port
            executor = Executors.newSingleThreadExecutor();

            executor.submit(() -> {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

                    out.println(clientIdToSend);

                    String line;
                    while ((line = in.readLine()) != null) {
                        receivedMessages.add(line);

                        if ("exit".equals(line)) {
                            break;
                        }
                    }
                } catch (IOException ignored) {
                }
            });
        }

        int getPort() {
            return serverSocket.getLocalPort();
        }

        List<String> getReceivedMessages() {
            return new ArrayList<>(receivedMessages);
        }

        @Override
        public void close() throws Exception {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (executor != null) {
                executor.shutdownNow();
            }
        }
    }
}