package ca.ucalgary.seng300.platform.server;

import java.io.IOException;

/**
 * Entry point to run the game server. Currently STUB: matchmaking in progress; platform core will implement.
 * When implemented: run this class, then run the JavaFX app and use "Host game" / "Join game" to connect.
 */
public class ServerMain {
    public static final int DEFAULT_PORT = 9090;

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        ServerCore server = new ServerCore(port);
        server.start();
        System.out.println("Game server (STUB). Matchmaking in progress — platform core will implement. Port " + port + " not listening :).");
    }
}
