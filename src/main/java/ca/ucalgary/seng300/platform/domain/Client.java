package ca.ucalgary.seng300.platform.domain;

import ca.ucalgary.seng300.platform.constants.ClientStatus;
import static ca.ucalgary.seng300.platform.constants.ClientStatus.*;

public class Client {
    ClientStatus clientStatus;

    public Client() {
        clientStatus = HOME;
    }

    public void joinMatchMakingQueue() {
        // NOTE: we can delete these print statements once we have
        // a notification system, or just keep them.
        IO.println("Joining matchmaking queue...");
        clientStatus = MATCHMAKING;
    }

    public void matchAssigned() {
        IO.println("Match found!");
        clientStatus = STARTING;
    }

    public void roomAssigned() {
        IO.println("Entering room.");
        clientStatus = IN_PROGRESS;
    }

    public void gameOver() {
        IO.println("Game over!");
        clientStatus = GAME_OVER;
    }

    public void returnToHome() {
        IO.println("Returning to home page...");
        clientStatus = HOME;
    }


}
