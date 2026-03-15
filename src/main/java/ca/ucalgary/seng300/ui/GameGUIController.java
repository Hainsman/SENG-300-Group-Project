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
    private TicTacToeController ticTacToeViewController; // Injects the child's controller
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
    private Label connect4Label;
    @FXML
    private Label tttLabel;
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

    public void hideGameScreens(GameGUI.GameViewData data){
        String gameType = data.gameType.toUpperCase();

        if(gameType.equals("TICTACTOE")){
            ticTacToeViewController.setVisibility(true);
            connect4ViewController.setVisibility(false);
            tttLabel.setVisible(true);
            connect4Label.setVisible(false);

        } else if(gameType.equals("CONNECT4")) {
            ticTacToeViewController.setVisibility(false);
            connect4ViewController.setVisibility(true);
            tttLabel.setVisible(false);
            connect4Label.setVisible(true);
        }

    }
}
