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
                TTTActionType.MOVE,
                new TTTActionPayload(row, col)
            );

            // TTTGame validates the move via TTTRuleValidator, then applies it to the state
            game.handleAction(action);


            // print board
            for (Player[] r : state.getBoard()) {
                System.out.println(java.util.Arrays.toString(r));
            }
            if(game.isGameOver()) {
                switch(state.getState()) {
                    case TIE: System.out.println("Game is a TIE!");
                    break;
                    case PLAYER_1_WIN: System.out.println("Player 1 Wins!");
                    break;
                    case PLAYER_2_WIN: System.out.println("Player 2 Wins!");
                    break;
                }
            }
        }
    }
}