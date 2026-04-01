package ca.ucalgary.seng300.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class GameDashboardNewController {

    @FXML
    void handleProfileDashboard(ActionEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.moveTo(CentralController.PROFILE_DASHBOARD_FXML);
    }

    /** Teammate account and accessibility settings. */
    @FXML
    void handleTeamSettings(ActionEvent event) {
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
