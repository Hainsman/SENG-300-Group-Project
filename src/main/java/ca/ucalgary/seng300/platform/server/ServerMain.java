package ca.ucalgary.seng300.platform.server;

import java.io.IOException;

/**
 * Entry point for the alternate line-based {@link ServerCore} prototype (port 9090 by default).
 * <p>The JavaFX client ({@link ca.ucalgary.seng300.platform.client.ClientCore}) uses object streams
 * against {@link ca.ucalgary.seng300.PlatformCore} on port <b>2020</b> — run {@code PlatformCore.main}
 * for multiplayer with the GUI.
 */
public class ServerMain {
//    public static final int DEFAULT_PORT = 9090;

    public static void main(String[] args) throws IOException {
//        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
//        System.out.println("Line-based ServerCore on port " + port + " (waits for 2 clients). GUI clients use PlatformCore on 2020.");
        ServerCoreMultiUser server = ServerCoreMultiUser.getInstance();
        server.start();
        System.out.println("ServerCore match finished or stopped.");
    }
}
