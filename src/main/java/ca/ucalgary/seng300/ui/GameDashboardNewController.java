package ca.ucalgary.seng300.ui;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class GameDashboardNewController {

    @FXML
    void handleOpenSettings(MouseEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.moveTo("Settings.fxml");
    }

    @FXML
    void C4click(MouseEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.selectedGame = "Connect 4";
        CentralController.moveTo("chooseModeNew.fxml");
    }

    @FXML
    void TTTclicked(MouseEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.selectedGame = "TicTacToe";
        CentralController.moveTo("chooseModeNew.fxml");
    }
}
