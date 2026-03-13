package ca.ucalgary.seng300.ui;
// Contributors: Kade Peters, Jarod Rideout

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;

public class GameGUIController {
    @FXML
    private VBox childView; // Injects the root of child.fxml
    @FXML
    private Connect4Controller connect4ViewController; // Injects the child's controller
    @FXML
    private TicTacToeController TicTacToeViewController; // Injects the child's controller
    @FXML
    public VBox historyBox;

    @FXML
    public void initialize() {
        // connect4ViewController.doSomething();
        GameGUI.setHistoryBox(historyBox);
    }
}
