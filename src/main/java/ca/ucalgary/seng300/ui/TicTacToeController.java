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

public class TicTacToeController extends GameGUI {

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

    private String[][] board = new String[3][3]; // stores "X", "O", or null
    private boolean xTurn = true; // X starts
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

    @FXML
    public void initialize() {
        status = GameStatus.IN_PROGRESS;
    }

    @Override
    public void render() {
        board = new String[3][3];
        moves = 0;
        xTurn = true;
        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button btn) {
                btn.setText("");
                btn.setStyle("");
                btn.setDisable(false);
            }
        }
        GameGUI.clearHistory();
    }

    @Override
    public void update() {
        if (turnLabel == null) {
            return;
        }
        switch (status) {
            case IN_PROGRESS -> turnLabel.setText(xTurn ? "X's Turn" : "O's Turn");
            case WIN -> turnLabel.setText((xTurn ? "O" : "X") + " Wins!");
            case DRAW -> turnLabel.setText("Draw!");
            default -> {
            }
        }
    }

    @Override
    protected void onGameCompletion() {
        disableBoard();
        resetButton.setVisible(true);
    }

    private String checkWinner() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != null &&
                    board[i][0].equals(board[i][1]) &&
                    board[i][1].equals(board[i][2])) {
                return board[i][0];
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (board[0][j] != null &&
                    board[0][j].equals(board[1][j]) &&
                    board[1][j].equals(board[2][j])) {
                return board[0][j];
            }
        }

        // Check diagonals
        if (board[0][0] != null &&
                board[0][0].equals(board[1][1]) &&
                board[1][1].equals(board[2][2])) {
            return board[0][0];
        }
        if (board[0][2] != null &&
                board[0][2].equals(board[1][1]) &&
                board[1][1].equals(board[2][0])) {
            return board[0][2];
        }

        return null;
    }

    @FXML
    private void handleClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        Integer row = GridPane.getRowIndex(clickedButton);
        Integer col = GridPane.getColumnIndex(clickedButton);
        if (row == null)
            row = 0;
        if (col == null)
            col = 0;

        if (clientCore != null) {
            GameStateUpdate update = clientCore.submitMoveSync(row, col);
            Player playerTurn = update.getGameState().getPlayerTurn();

            if (update.getGameState() != null) {
                String playerHistory;
                Color playerColor;
                if (Player.PLAYER_1.equals(playerTurn)) {
                    playerHistory = X;
                    playerColor = XColor;
                } else {
                    playerHistory = O;
                    playerColor = OColor;
                }

                GameGUI.addHistory(GAME, playerHistory, indexToCoordinates(row, col), playerColor);
                refreshFromGameStateUpdate(update);
            }
            return;
        }
        if (session != null) {
            if (!session.submitMove(row, col))
                return;
            // getTurn() is now the next player; the one who moved is the other
            String mover;
            Color moverColor;
            if (session.getTurn().equals(Player.PLAYER_1)){
                mover = X;
                moverColor = XColor;
            } else {
                mover = O;
                moverColor = OColor;
            }
            GameGUI.addHistory(GAME, mover, indexToCoordinates(row, col), moverColor);
            refreshFromSession();
            return;
        }

        if (board[row][col] != null)
            return; // already filled

        // Used for sending match history
        String rowString = String.valueOf(row + 1);
        String colString = String.valueOf(col + 1);

        // Set X or O
        if (xTurn) {
            clickedButton.setText(X);
            clickedButton.setStyle(X_STYLE);
            board[row][col] = X;
            GameGUI.addHistory(GAME, X, indexToCoordinates(row, col), XColor);
        } else {
            clickedButton.setText(O);
            clickedButton.setStyle(O_STYLE);
            board[row][col] = O;
            GameGUI.addHistory(GAME, O, indexToCoordinates(row, col), OColor);
        }

        moves++;

        // Check winner
        String winner = checkWinner();
        if (winner != null) {
            turnLabel.setText(winner + " Wins!");
            onGameCompletion();
            return;
        }

        // Check draw
        if (moves == 9) {
            turnLabel.setText("Draw!");
            onGameCompletion();
            return;
        }

        else {
            // Switch turn
            xTurn = !xTurn;
            turnLabel.setText(xTurn ? "X's Turn" : "O's Turn");
        }
        update();
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
        board = new String[3][3];
        moves = 0;
        xTurn = true;
        turnLabel.setText(X_TURN);
        GameGUI.clearHistory();

        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setText(PLACEHOLDER);
                btn.setStyle(PLACEHOLDER);
                btn.setDisable(false);
            }
        }
    }

    /**
     * Updates grid and turn label from session state (only when session is set).
     */
    private void refreshFromSession() {
        if (session == null)
            return;
        Player[][] b = session.getBoard();
        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                Integer r = GridPane.getRowIndex(btn);
                Integer c = GridPane.getColumnIndex(btn);
                if (r == null) r = 0;
                if (c == null) c = 0;
                if (b[r][c].equals(Player.PLAYER_1)) {
                    btn.setText(X);
                    btn.setStyle(X_STYLE);
                } else if (b[r][c].equals(Player.PLAYER_2)) {
                    btn.setText(O);
                    btn.setStyle(O_STYLE);
                } else {
                    btn.setText(PLACEHOLDER);
                    btn.setStyle(PLACEHOLDER);
                }
                btn.setDisable(session.isGameOver());
            }
        }
        State st = session.getState();
        if (st.equals(State.PLAYER_1_WIN))
            turnLabel.setText(X_WIN);
        if (st.equals(State.PLAYER_2_WIN))
            turnLabel.setText(O_WIN);
        if (st.equals(State.TIE))
            turnLabel.setText(DRAW);
        if (session.getTurn().equals(Player.PLAYER_1))
            turnLabel.setText(X_TURN);
        if (session.getTurn().equals(Player.PLAYER_2))
            turnLabel.setText(O_TURN);
    }

    public void setSession(TicTacToeSession s) {
        this.session = s;
        if (s != null)
            refreshFromSession();
    }

    /**
     * Use when in online mode: refresh grid and label from server update.
     */
    public void refreshFromGameStateUpdate(GameStateUpdate update) {
        if (update == null || board == null)
            return;

        State status = update.getGameState().getState();
        updateBoard(update);

        turnLabel.setText(getStateAsStr(status));

        if (update.getMessage() != null && !update.getMessage().isEmpty()) {
            turnLabel.setText(turnLabel.getText() + " (" + update.getMessage() + ")");
        }
    }

    public void setClientCore(ClientCore c) {
        this.clientCore = c;
        if (c != null && c.getLastUpdate() != null)
            refreshFromGameStateUpdate(c.getLastUpdate());
    }

    public void setVisibility(Boolean status) {
        boardPane.setVisible(status);
    }

    private String getStateAsStr(State state) {
        String message;
        if (State.PLAYER_1_WIN.equals(state))
            message = X_WIN;
        if (State.PLAYER_2_WIN.equals(state))
            message = O_WIN;
        if (State.TIE.equals(state))
            message = DRAW;
        if (State.IN_PROGRESS.equals(state))
            message = X_TURN;
        else
            message = O_TURN;

        return message;
    }

    private void updateBoard(GameStateUpdate gameStateUpdate) {
        Player[][] board = gameStateUpdate.getGameState().getBoard();
        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                Integer r = GridPane.getRowIndex(btn);
                Integer c = GridPane.getColumnIndex(btn);
                if (r == null)
                    r = 0;
                if (c == null)
                    c = 0;

                Player cell = r < board.length && c < board[r].length ?  board[r][c]: Player.EMPTY;
                if (Player.PLAYER_1.equals(cell)) {
                    btn.setText(X);
                    btn.setStyle(X_STYLE);
                } else if (Player.PLAYER_2.equals(cell)) {
                    btn.setText(O);
                    btn.setStyle(O_STYLE);
                } else {
                    btn.setText(PLACEHOLDER);
                    btn.setStyle(PLACEHOLDER);
                }
            }
        }
    }

    private String indexToCoordinates(int row, int column){
        row += OFF_SET;
        column += OFF_SET;
        return "("+row+", "+column+")";
    }
}
