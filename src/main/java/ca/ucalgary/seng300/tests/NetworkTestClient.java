// INFO: Use this test together with NetworkTestServer to try functionality!

package ca.ucalgary.seng300.tests;

import ca.ucalgary.seng300.platform.client.ClientCore;

public class NetworkTestClient {
    static void main(String[] args) {
        // We can change the hostname later for fun...
        ClientCore c = new ClientCore("localhost", 9090);
        c.connect();
    }
}
