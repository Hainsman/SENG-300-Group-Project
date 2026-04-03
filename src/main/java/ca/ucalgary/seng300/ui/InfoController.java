package ca.ucalgary.seng300.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class InfoController {


    @FXML
    private Button backButton;

    @FXML
    private Label gameTitleLabel;

    @FXML
    private TextArea rulesTextArea;

    private String gameType;

    @FXML
    private void handleBack (ActionEvent event) {
        if (CentralController.previousScreen != null) {
            CentralController.moveTo(CentralController.previousScreen);
        } else {
            CentralController.moveTo("GameDashboardNew.fxml");
        }

    }

    public void setGameType(String gameType){
        gameTitleLabel.setText(gameType);

        if("TICTACTOE".equals(gameType)){
            rulesTextArea.setText(
                    "TicTacToe Rules:\n" +
                            "- Players take turns placing X and O.\n" +
                            "- First to get 3 in a row (horizontal, vertical, diagonal) wins.\n" +
                            "- If all spaces are filled and no player has 3 in a row, the game is a draw."
            );
        } else if ("CONNECT4".equals(gameType)){
            rulesTextArea.setText(
                    "Connect 4 Rules:\n" +
                            "- Players take turns dropping colored discs into a 7x6 grid.\n" +
                            "- First to connect 4 discs vertically, horizontally, or diagonally wins.\n" +
                            "- If the grid is full and no player has 4 in a row, the game is a draw."
            );
        } else{
            rulesTextArea.setText("Rules unavailable for this game");

        }

    }


}
