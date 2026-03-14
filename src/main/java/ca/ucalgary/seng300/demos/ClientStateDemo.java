package ca.ucalgary.seng300.demos;

import ca.ucalgary.seng300.platform.domain.Client;

public class ClientStateDemo {
    static void main() {
        Client me = new Client();
        me.joinMatchMakingQueue();
        me.matchAssigned();
        me.roomAssigned();
        me.gameOver();
        me.returnToHome();
    }
}
