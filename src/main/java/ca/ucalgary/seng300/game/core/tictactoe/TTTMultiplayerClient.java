// INFO: Use this test together with NetworkTestServer to try functionality!

package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.platform.client.ClientCore;

import java.io.IOException;
import java.util.Scanner;

public class TTTMultiplayerClient {
    static volatile String boardState;
    static volatile boolean done;
    static volatile boolean valid = false;
    static volatile String gameStatus = "";

    public void run(String playerID, String host, int port) {
        // We can change the hostname later for fun...
        ClientCore c = new ClientCore(host, port);
        c.myPlayerId = playerID;
        c.connect();
        System.out.println(playerID + "'s client started up successfully!");

        Scanner input = new Scanner(System.in);
        while (true) {
            done = false;
            valid = false;
            Thread boardUpdate = new Thread(() -> {
                boardState = readBoard(c);
                try {
                    gameStatus = c.in.readLine();
                } catch (IOException _) {}
                done = true;
            });
            boardUpdate.start();
            while (!done) {
                Thread.onSpinWait();
            }

            switch (gameStatus) {
                case "TIE":
                    System.out.println("Tie game!");
                    return;
                case "P1_WIN":
                    System.out.println("Player 1 wins!");
                    return;
                case "P2_WIN":
                    System.out.println("Player 2 wins!");
                    return;
                case "IN_PROGRESS":
                    break;
                default:
                    System.err.println("ERROR: couldn't get game status.");
                    return;
            }

            while (!valid) {
                System.out.println(boardState);
                System.out.println("Which row would you like to play in? (0-2)");
                int row = input.nextInt();
                System.out.println("Which column would you like to play in? (0-2)");
                int col = input.nextInt();
                c.submitMove(row, col);

                done = false;
                Thread isValid = new Thread(() -> {
                    try {
                        valid = c.in.readLine().equals("VALID");
                        if (!valid)
                            System.out.println("Invalid move... try again.");
                        done = true;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                isValid.start();
                while (!done) {
                    Thread.onSpinWait();
                }
            }

            System.out.println("Valid move!");
        }

    }

    static String readBoard(ClientCore c) {
        StringBuilder board = new StringBuilder();
        try {
            for (int i = 0; i < 4; i++) {
                board.append(c.in.readLine()).append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return board.toString();
    }
}
