// INFO: Use this test together with NetworkTestServer to try functionality!

package ca.ucalgary.seng300.tests;

import ca.ucalgary.seng300.platform.client.ClientCore;

import java.io.IOException;
import java.util.Scanner;

public class NetworkTestClientBob {
    static volatile String boardState;

    static void main(String[] args) {
        // We can change the hostname later for fun...
        ClientCore c = new ClientCore("localhost", 9090);
        c.myPlayerId = "1";
        c.connect();
        System.out.println("Bob's client started up successfully!");

        Scanner input = new Scanner(System.in);
        boolean runGame = true;
        boolean moveAccepted = false;
        while (runGame) {
            Thread waitForMyTurn = new Thread(() -> {
                boardState = readBoard(c);
            });
            waitForMyTurn.start();
            while (boardState == null) {
                Thread.onSpinWait();
            }
            System.out.println(boardState);
            System.out.println("Which row would you like to play in? (1-3)");
            int row = input.nextInt();
            System.out.println("Which column would you like to play in? (1-3)");
            int col = input.nextInt();
            c.submitMove(row, col);
        }

    }

    static String readBoard(ClientCore c) {
        StringBuilder board = new StringBuilder();
        try {
            for (int i = 0; i < 3; i++) {
                board.append(c.in.readLine()).append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return board.toString();
    }
}
