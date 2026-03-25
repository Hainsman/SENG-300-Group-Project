package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.messages.GameStateUpdate;
import ca.ucalgary.seng300.platform.session.TicTacToeSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

public class TicTacToeController {

    private CentralController boss;
    public void setBoss(CentralController c){
        boss = c;
        boss.setTic(this);
        clientCore = boss.getCC();
    }


    /** When set, moves and state use the game core session; when null, existing local board logic is used. */
    private TicTacToeSession session = null;
    /** When set, moves are sent to the server and UI is refreshed from GameStateUpdate (online mode). */
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

    private Player[][] board = new Player[3][3]; // stores PLAYER_1 PLAYER_2 NULL
    private Player myPlayer = Player.PLAYER_1;    // which player this user is, PLAYER_1 or PLAYER_2
    private boolean xTurn = true; // X starts
    private int moves = 0;

    private static final Color xColor = Color.web("#3B82F6");
    private static final Color oColor = Color.web("#A855F7");
    private String player1Token = "X";
    private String player2Token = "O";

    private String getToken (Player p){
        if(p.equals(Player.PLAYER_1)){
            return player1Token;
        }
        else if (p.equals(Player.PLAYER_2)){
            return player2Token;
        }
        else{
            // this should probably never happen, but if null needs a token it goes here
            return "ERR";
            //return "";
        }
    }

    private String checkWinner() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (!board[i][0].equals(Player.EMPTY) &&
                    board[i][0].equals(board[i][1]) &&
                    board[i][1].equals(board[i][2])) {
                return getToken(board[i][0]);
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (!board[0][j].equals(Player.EMPTY) &&
                    board[0][j].equals(board[1][j]) &&
                    board[1][j].equals(board[2][j])) {
                return getToken(board[0][j]);
            }
        }

        // Check diagonals
        if (!board[0][0].equals(Player.EMPTY) &&
                board[0][0].equals(board[1][1]) &&
                board[1][1].equals(board[2][2])) {
            return getToken(board[0][0]);
        }
        if (!board[0][2].equals(Player.EMPTY) &&
                board[0][2].equals(board[1][1]) &&
                board[1][1].equals(board[2][0])) {
            return getToken(board[0][2]);
        }

        return null;
    }

    @FXML
    private void handleClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        Integer row = GridPane.getRowIndex(clickedButton);
        Integer col = GridPane.getColumnIndex(clickedButton);
        if (row == null) row = 0;
        if (col == null) col = 0;
        boolean upd = clientCore.submitMove(row, col);
        /*
        if (clientCore != null) {
            GameStateUpdate update = clientCore.submitMoveSync(row, col);
            if (update != null) {
                GameGUI.addHistory("TicTacToe", "PLAYER_1".equals(update.getCurrentTurn()) ? "O" : "X",
                        "(" + (row + 1) + ", " + (col + 1) + ")", "PLAYER_1".equals(update.getCurrentTurn()) ? oColor : xColor);
                //refreshFromGameStateUpdate(update);
            }
            return;
        }
        if (session != null) {
            if (!session.submitMove(row, col)) return;
            // getTurn() is now the next player; the one who moved is the other
            String mover = session.getTurn() == Player.PLAYER_1 ? "O" : "X";
            Color moverColor = session.getTurn() == Player.PLAYER_1 ? oColor : xColor;
            GameGUI.addHistory("TicTacToe", mover, "(" + (row + 1) + ", " + (col + 1) + ")", moverColor);
            refreshFromSession();
            return;
        }

        if (board[row][col] != null)
            return; // already filled

        // Set X or O
        //if (xTurn) {
            clickedButton.setText(getToken(myPlayer));
            clickedButton.setStyle("-fx-text-fill:#3B82F6; -fx-font-size:40px; -fx-font-weight:bold;");
            board[row][col] = myPlayer;
            GameGUI.addHistory("TicTacToe", "X",
                    "(" + (row + 1) + ", " + (col + 1) + ")", xColor);
        } else {
            clickedButton.setText("O");
            clickedButton.setStyle("-fx-text-fill:#A855F7; -fx-font-size:40px; -fx-font-weight:bold;");
            board[row][col] = "O";
            GameGUI.addHistory("TicTacToe", "O",
                    "(" + (row + 1) + ", " + (col + 1) + ")", oColor);
        }*/

        moves++;

        // Check winner
        String winner = checkWinner();
        if (winner != null) {
            turnLabel.setText(winner + " Wins!");
            disableBoard();
            return;
        }

        // Check draw
        if (moves == 9) {
            turnLabel.setText("Draw!");
            return;
        }

        // Switch turn
        xTurn = !xTurn;
        turnLabel.setText(xTurn ? "X's Turn" : "O's Turn");
        if(myPlayer.equals(Player.PLAYER_1)){
            myPlayer = Player.PLAYER_2;
        }
        else{
            myPlayer = Player.PLAYER_1;
        }
    }

    private void disableBoard() {
        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button) {
                node.setDisable(true);
            }
        }
    }

    @FXML
    void resetClick(ActionEvent event) {
        if (session != null) {
            session.startNewGame();
            GameGUI.clearHistory();
            refreshFromSession();
            return;
        }
        board = new Player[3][3];
        moves = 0;
        xTurn = true;
        turnLabel.setText("X's Turn");
        GameGUI.clearHistory();

        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setText("");
                btn.setStyle("");
                btn.setDisable(false);
            }
        }
    }

    /** Updates grid and turn label from session state (only when session is set). */
    private void refreshFromSession() {
        if (session == null) return;
        ca.ucalgary.seng300.game.core.Player[][] b = session.getBoard();
        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                Integer r = GridPane.getRowIndex(btn);
                Integer c = GridPane.getColumnIndex(btn);
                if (r == null) r = 0;
                if (c == null) c = 0;
                if (b[r][c] == Player.PLAYER_1) {
                    btn.setText("X");
                    btn.setStyle("-fx-text-fill:#3B82F6; -fx-font-size:40px; -fx-font-weight:bold;");
                } else if (b[r][c] == Player.PLAYER_2) {
                    btn.setText("O");
                    btn.setStyle("-fx-text-fill:#A855F7; -fx-font-size:40px; -fx-font-weight:bold;");
                } else {
                    btn.setText("");
                    btn.setStyle("");
                }
                btn.setDisable(session.isGameOver());
            }
        }
        State st = session.getState();
        if (st == State.PLAYER_1_WIN) turnLabel.setText("X Wins!");
        else if (st == State.PLAYER_2_WIN) turnLabel.setText("O Wins!");
        else if (st == State.TIE) turnLabel.setText("Draw!");
        else turnLabel.setText(session.getTurn() == Player.PLAYER_1 ? "X's Turn" : "O's Turn");
    }

    public void setSession(TicTacToeSession s) {
        this.session = s;
        if (s != null) refreshFromSession();
    }

    /** Use when in online mode: refresh grid and label from server update. */
    public void refreshFromGameStateUpdate(TTTGameState update) {
        if (update == null || update.getBoard() == null) return;
        System.out.println("Ui update in TTTcontroller");
        board = update.getBoard();
        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                Integer r = GridPane.getRowIndex(btn);
                Integer c = GridPane.getColumnIndex(btn);

                if (r == null) r = 0;
                if (c == null) c = 0;
                // Don't know if there is a reason not to set the style for empty buttons

                btn.setText(getToken(board[r][c]));
                System.out.println("button updated as "+getToken(board[r][c]));
                if(!board[r][c].equals(Player.EMPTY)) {
                    btn.setStyle("-fx-text-fill:#3B82F6; -fx-font-size:40px; -fx-font-weight:bold;");
                }
                /*
                if (r == null) r = 0;
                if (c == null) c = 0;
                Player cell = r < b.length && c < b[r].length ? b[r][c] : Player.EMPTY;

                if (Player.PLAYER_1.equals(cell)) {
                    btn.setText("X");
                    btn.setStyle("-fx-text-fill:#3B82F6; -fx-font-size:40px; -fx-font-weight:bold;");
                } else if ("PLAYER_2".equals(cell)) {
                    btn.setText("O");
                    btn.setStyle("-fx-text-fill:#A855F7; -fx-font-size:40px; -fx-font-weight:bold;");
                } else {
                    btn.setText("");
                    btn.setStyle("");
                }*/
                //btn.();
                boolean gameOver = !State.IN_PROGRESS.equals(update.getState());
                btn.setDisable(gameOver);
            }
        }
        State status = update.getState();
        if (State.PLAYER_1_WIN.equals(status)) turnLabel.setText("X Wins!");
        else if (State.PLAYER_2_WIN.equals(status)) turnLabel.setText("O Wins!");
        else if (State.TIE.equals(status)) turnLabel.setText("Draw!");
        else turnLabel.setText(getToken(update.getPlayerTurn()));
        /* commented out depending on merging gamestate and gamestateupdate functions
        if (update.getMessage() != null && !update.getMessage().isEmpty()) {
            turnLabel.setText(turnLabel.getText() + " (" + update.getMessage() + ")");
        }*/
    }

    /*public void setClientCore(ClientCore c) {
        this.clientCore = c;
        if (c != null && c.getLastUpdate() != null) refreshFromGameStateUpdate(c.getLastUpdate());
    }*/

    public void setVisibility(Boolean status){
        boardPane.setVisible(status);
    }
}
