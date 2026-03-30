package ca.ucalgary.seng300.game.core.tictactoe;

import java.util.Scanner;
import ca.ucalgary.seng300.game.core.Player;

public class TTTCLI {

    public static void main(String[] args) {
        // Create the game
        TTTGame game =new TTTGame();
        TTTGameState state = (TTTGameState) game.getGameState();
        state.getBoard(); // initialize the board 

        Scanner scanner = new Scanner(System.in);

        while (!game.isGameOver()) {
             // get move from user
            System.out.print("Enter row col: ");
            int row =scanner.nextInt();
            int col =scanner.nextInt();

            // build and send action to game
            TTTPlayerAction action = new TTTPlayerAction(
                state.getPlayerTurn().toString(),
                "MOVE",
                row,
                col
            );

            // TTTGame validates the move via TTTRuleValidator, then applies it to the state
            game.handleAction(action);

            // print board
            for (Player[] r : state.getBoard())
                System.out.println(java.util.Arrays.toString(r));
        }

        System.out.println("Game Over! State: " +state.getState());
        scanner.close();
    }
}