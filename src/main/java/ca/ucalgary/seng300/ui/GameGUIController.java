package ca.ucalgary.seng300.ui;
// Contributors: Kade Peters, Jarod Rideout

import javafx.application.Platform;
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
    private Label eloValue; // Value of elo to be displayed
    @FXML
    private Label winValue; // Elo change based on win
    @FXML
    private Label lossValue; // Elo change based on loss
    @FXML
    private Label drawValue; // Elo change based on draw
    @FXML
    private Label rankValue; // Elo description string
    @FXML
    public VBox historyBox;

    @FXML
    private Button infoButton;

    @FXML
    public void initialize() {
        GameGUI.setHistoryBox(historyBox);
        GameGUI.setEloLabels(eloValue, winValue, lossValue, drawValue, rankValue);
    }

    @FXML
    private void handleInfoButton(ActionEvent event) {
        CentralController.moveTo("Info.fxml");

    }

    @FXML
    private void handleExitButton(ActionEvent event) {
        Platform.exit();
    }
}
