package ca.ucalgary.seng300.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class Info extends BorderPane implements Navigable {


    @FXML private Button backButton;
    @FXML private Label gameTitleLabel;
    @FXML private TextArea rulesTextArea;

    private String gameType;

    public Info() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ca.ucalgary.seng300.ui/" + getNavigation().getFxml()));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack (ActionEvent event) {
        if (CentralController.previousScreen != null) {
            CentralController.moveTo(CentralController.previousScreen);
        } else {
            CentralController.moveTo(Navigation.GAME_DASHBOARD);
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

    @Override
    public Navigation getNavigation() {
        return Navigation.INFO;
    }

    @Override
    public void reset() {

    }
}
