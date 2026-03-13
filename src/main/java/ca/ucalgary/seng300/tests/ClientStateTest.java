package ca.ucalgary.seng300.tests;

import ca.ucalgary.seng300.platform.domain.Client;

public class ClientStateTest {
    static void main() {
        Client me = new Client();
        me.joinMatchMakingQueue();
        me.matchAssigned();
        me.roomAssigned();
        me.gameOver();
        me.returnToHome();
    }
}
