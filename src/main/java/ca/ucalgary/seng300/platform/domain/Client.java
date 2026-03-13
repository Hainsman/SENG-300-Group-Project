package ca.ucalgary.seng300.platform.domain;

import ca.ucalgary.seng300.platform.constants.ClientStatus;
import static ca.ucalgary.seng300.platform.constants.ClientStatus.*;

public class Client {
    ClientStatus clientStatus;

    Client() {
        clientStatus = LOBBY;
    }
}
