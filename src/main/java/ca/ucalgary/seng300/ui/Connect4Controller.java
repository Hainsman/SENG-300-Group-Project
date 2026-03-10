package ca.ucalgary.seng300.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

public class Connect4Controller {

    @FXML
    private GridPane gridPane;
    @FXML
    private Label statusLabel;

    private static final int rows = 6;
    private static final int cols = 7;
    private static final double radius = 40;

    private Circle[][] circles = new Circle[rows][cols];

    private final Color EMPTY = Color.web("#EEEEEE");
    private final Color P1 = Color.web("#c45656ff");
    private final Color P2 = Color.web("#b19018ff");
    private final Color STROKE = Color.web("#0f0f1a");

    @FXML
    public void initialize() {
        buildBoard();
    }

    private void buildBoard() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Circle circle = new Circle(40);
                circle.setFill(Color.WHITE);
                circles[row][col] = circle;
                gridPane.add(circle, col, row);
            }
        }
    }
}
