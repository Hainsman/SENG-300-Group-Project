package ca.ucalgary.seng300.game.core.connect4;

import java.util.Scanner;
import ca.ucalgary.seng300.game.core.Player;

public class C4CLI {
    public static void main(String[] args) {
        // Creates the game
        C4Game game =new C4Game();
        C4GameState state =(C4GameState) game.getGameState();
        state.getBoard();// initialize the board
        Scanner scanner =new Scanner(System.in);

        while (!game.isGameOver()) {
            // Print the board
            System.out.println("\nCurrent Board:");
            for (Player[] r :state.getBoard())
                System.out.println(java.util.Arrays.toString(r));

            // Shows whose turn it is
            System.out.println("Player turn: " +state.getPlayerTurn());

            // Get user input for column
            System.out.print("Enter col (0-6): ");
            int col = scanner.nextInt();

            // Create the action based on user input
            C4PlayerAction action =new C4PlayerAction(
                state.getPlayerTurn().toString(),
                C4ActionType.MOVE,
                -1,
                col
            );

            // Bypass validator for now, use applyAction directly
            state.applyAction(action);
        }

        //Print final board
        System.out.println("\nFinal Board:");
        for (Player[] r : state.getBoard())
            System.out.println(java.util.Arrays.toString(r));

        System.out.println("Game Over! State: " +state.getState());
        scanner.close();
    }
}
