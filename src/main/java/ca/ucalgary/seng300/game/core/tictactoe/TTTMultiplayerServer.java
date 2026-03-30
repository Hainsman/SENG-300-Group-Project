// Instructions:
// 1. Run TTT Multiplayer Server
// 2. Run TTT Multiplayer Client 1
// 3. Run TTT Multiplayer Client 2
// 4. Enjoy your game of Tic-Tac-Toe!

package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.platform.server.ServerCore;

import java.io.IOException;

public class TTTMultiplayerServer {
    public static void main(String[] args) {
        ServerCore s = new ServerCore(9090);
        System.out.println("Started up server successfully!");
        try { s.start(); } catch(IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
        s.stop();
    }
}
