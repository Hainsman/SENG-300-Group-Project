package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.messages.GameStateUpdate;
import ca.ucalgary.seng300.platform.session.TicTacToeSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.UUID;


public class TicTacToeController2 {

    /**
     * When set, moves and state use the game core session; when null, existing local board logic is used.
     */
    private TicTacToeSession session = null;
    /**
     * When set, moves are sent to the server and UI is refreshed from GameStateUpdate (online mode).
     */
    private ClientCore clientCore = null;

    @FXML
    private StackPane boardPane;

    @FXML
    private Button cell00;

    @FXML
    private Button cell01;

    @FXML
    private Button cell02;

    @FXML
    private Button cell10;

    @FXML
    private Button cell11;

    @FXML
    private Button cell12;

    @FXML
    private Button cell20;

    @FXML
    private Button cell21;

    @FXML
    private Button cell22;

    @FXML
    private GridPane ticTacToeGrid;

    @FXML
    private Label turnLabel;

    @FXML
    private Button resetButton;

    private Player[][] board = new Player[3][3]; // stores "X", "O", or null
    private int moves = 0;

    private static final Color XColor = Color.web("#3B82F6");
    private static final Color OColor = Color.web("#A855F7");
    private static final int MAX_MOVES = 9;
    private static final int OFF_SET = 1;
    private static final String X_STYLE = "-fx-text-fill:#3B82F6; -fx-font-size:40px; -fx-font-weight:bold;";
    private static final String O_STYLE = "-fx-text-fill:#A855F7; -fx-font-size:40px; -fx-font-weight:bold;";
    private static final String GAME = "TicTacToe";
    private static final String O = "O";
    private static final String X = "X";
    private static final String O_TURN = "O's Turn";
    private static final String X_TURN = "X's Turn";
    private static final String X_WIN = "X Wins!";
    private static final String O_WIN = "O Wins!";
    private static final String DRAW = "Draw!";
    private static final String WINS = "Wins!";
    private static final String PLACEHOLDER = "";
    private TTTGameState ticTacToeGameState;
    private Player playerTurn;
    private Color playerColor;

    private void handleClick(ActionEvent event){
        if (clientCore == null)
            return;

        Button clickedButton = (Button) event.getSource();
        Integer row = GridPane.getRowIndex(clickedButton);
        Integer col = GridPane.getColumnIndex(clickedButton);

        GameStateUpdate gsu = new GameStateUpdate(UUID.randomUUID(), new TTTGameState(), PLACEHOLDER); //create instance of game state
        Player playerTurn = gsu.getGameState().getPlayerTurn(); // get instance of the playerTurn

        if (playerTurn.equals(Player.PLAYER_1)){

        } else{

        }



    }


}
