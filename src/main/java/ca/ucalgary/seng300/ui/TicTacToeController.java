package ca.ucalgary.seng300.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

public class TicTacToeController extends GameGUI {

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

    private static final Color xColor = Color.web("#3B82F6");
    private static final Color oColor = Color.web("#A855F7");

    @FXML
    public void initialize() {
        status = GameStatus.IN_PROGRESS;
    }

    @Override
    public void render() {
        board = new String[3][3];
        moves = 0;
        xTurn = true;
        for (Node node: ticTacToeGrid.getChildren()) {
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
            default -> {}
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

        if (board[row][col] != null)
            return; // already filled

        // Set X or O
        if (xTurn) {
            clickedButton.setText("X");
            clickedButton.setStyle("-fx-text-fill:#3B82F6; -fx-font-size:40px; -fx-font-weight:bold;");
            board[row][col] = "X";
            GameGUI.addHistory("TicTacToe", "X",
                    "(" + (row + 1) + ", " + (col + 1) + ")", xColor);
        } else {
            clickedButton.setText("O");
            clickedButton.setStyle("-fx-text-fill:#A855F7; -fx-font-size:40px; -fx-font-weight:bold;");
            board[row][col] = "O";
            GameGUI.addHistory("TicTacToe", "O",
                    "(" + (row + 1) + ", " + (col + 1) + ")", oColor);
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
        resetButton.setVisible(false);
        status = GameStatus.IN_PROGRESS;
        render();
        update();
    }

    public void setVisibility(Boolean status){
        boardPane.setVisible(status);
    }
}
