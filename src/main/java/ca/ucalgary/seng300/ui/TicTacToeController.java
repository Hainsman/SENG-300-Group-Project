package ca.ucalgary.seng300.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;

public class TicTacToeController {

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
    private Button resetButton;

    private String[][] board = new String[3][3]; // stores "X", "O", or null
    private boolean xTurn = true; // X starts

    private String checkWinner() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != null && board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2])) {
                return board[i][0];
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (board[0][j] != null && board[0][j].equals(board[1][j]) && board[1][j].equals(board[2][j])) {
                return board[0][j];
            }
        }

        // Check diagonals
        if (board[0][0] != null && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) {
            return board[0][0];
        }
        if (board[0][2] != null && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0])) {
            return board[0][2];
        }

        // No winner
        return null;
    }

    @FXML
    private void handleClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        int row = GridPane.getRowIndex(clickedButton);
        int col = GridPane.getColumnIndex(clickedButton);

        // If cell is empty
        if (board[row][col] == null) {
            if (xTurn) {
                clickedButton.setText("X");
                board[row][col] = "X";
            } else {
                clickedButton.setText("O");
                board[row][col] = "O";
            }
            xTurn = !xTurn; // switch turn
        }
        String winner = checkWinner();
    }

    @FXML
    void resetClick(ActionEvent event) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = null;
            }
        }

        for (Node node : ticTacToeGrid.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setText("");
                ((Button) node).setStyle(""); // remove X/O styles
            }
        }
        xTurn = true;
    }
}
