// INFO: Use this test together with NetworkTestClient to try functionality!

package ca.ucalgary.seng300.tests;

import ca.ucalgary.seng300.platform.server.ServerCore;

import java.io.IOException;

public class NetworkTestServer {
    static void main(String[] args) {
        ServerCore s = new ServerCore(9090);
        try { s.start(); } catch(IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
}
