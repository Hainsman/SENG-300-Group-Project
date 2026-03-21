package ca.ucalgary.seng300.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.event.ActionEvent;

public class Connect4Controller extends GameGUI {

    @FXML
    private StackPane boardPane;

    @FXML
    private GridPane gridPane;
    private Circle[][] circles = new Circle[6][7];
    private int rows = 6;
    private int cols = 7;
    @FXML
    private Label statusLabel;

    @FXML
    private Button resetButton;

    private final Color P1 = Color.web("#dd3030ff");
    private final Color P2 = Color.web("#fad44aff");

    @FXML
    public void initialize() {
        render();
    }



    @Override
    public void render() {
        gridPane.getChildren().clear();
        circles = new Circle[6][7];
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Circle circle = new Circle(40);
                circle.setFill(Color.WHITE);
                circles[row][col] = circle;

                final int c = col;
                circle.setOnMouseClicked(e -> dropPiece(c));

                gridPane.add(circle, col, row);
            }
        }
    }

    @Override
    public void update() {
        if (statusLabel == null) return;
        switch (status) {
            case IN_PROGRESS -> statusLabel.setText("Player " + currentPlayer + "'s Turn");
            case WIN         -> statusLabel.setText("Player " + currentPlayer + " Wins!");
            case DRAW        -> statusLabel.setText("It's a Draw!");
            default          -> {}
        }

    }

    @Override
    protected void onGameCompletion() {
        disableBoard();
        resetButton.setVisible(true);
    }

    private int currentPlayer = 1;

    private void dropPiece(int col) {
        for (int row = 5; row >= 0; row--) {
            if (circles[row][col].getFill().equals(Color.WHITE)) {
                Color playerColor;
                if (currentPlayer == 1) {
                    playerColor = P1;
                    GameGUI.addHistory("Connect4", "1",
                            "(" + (row + 1) + ", " + (col + 1) + ")", playerColor);
                } else {
                    playerColor = P2;
                    GameGUI.addHistory("Connect4", "2",
                            "(" + (row + 1) + ", " + (col + 1) + ")", playerColor);
                }
                circles[row][col].setFill(playerColor);

                if (checkWin(row, col, playerColor)) {
                    status = GameStatus.WIN;
                    onGameCompletion();
                } else if (isBoardFull()) {
                    status = GameStatus.DRAW;
                    onGameCompletion();
                } else {
                    status = GameStatus.IN_PROGRESS;
                    if (currentPlayer == 1) {
                        currentPlayer = 2;
                    } else {
                        currentPlayer = 1;
                    }
                }
                update();
                return;
            }
        }
    }

    private boolean checkWin(int row, int col, Color color) {
        return checkDirection(row, col, color, 1, 0) // horizontal
                || checkDirection(row, col, color, 0, 1) // vertical
                || checkDirection(row, col, color, 1, 1) // diagonal ↘
                || checkDirection(row, col, color, 1, -1); // diagonal ↙
    }

    private boolean checkDirection(int row, int col, Color color, int dr, int dc) {
        int count = 1;
        count += countFrom(row, col, color, dr, dc);
        count += countFrom(row, col, color, -dr, -dc);
        return count >= 4;
    }

    private int countFrom(int row, int col, Color color, int dr, int dc) {
        int count = 0;
        int r = row + dr;
        int c = col + dc;
        while (r >= 0 && r < rows && c >= 0 && c < cols
                && circles[r][c].getFill().equals(color)) {
            count++;
            r += dr;
            c += dc;
        }
        return count;
    }

    private boolean isBoardFull() {
        for (int col = 0; col < cols; col++)
            if (circles[0][col].getFill().equals(Color.WHITE))
                return false;
        return true;
    }

    private void disableBoard() {
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                circles[row][col].setOnMouseClicked(null);
    }

    public void setVisibility(Boolean status) {
        boardPane.setVisible(status);
    }

    @FXML
    private void resetClick(ActionEvent event) {
        resetButton.setVisible(false);
        status = GameStatus.IN_PROGRESS;
        currentPlayer = 1;
        render();
        update();
    }
}
