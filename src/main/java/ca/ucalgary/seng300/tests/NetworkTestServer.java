// INFO: Use this test together with NetworkTestClientAlice to try functionality!

package ca.ucalgary.seng300.tests;

import ca.ucalgary.seng300.platform.server.ServerCore;

import java.io.IOException;

public class NetworkTestServer {
    static void main(String[] args) {
        ServerCore s = new ServerCore(9090);
        System.out.println("Started up server successfully!");
        try { s.start(); } catch(IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
        s.stop();
    }
}
