package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.constants.Game;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

public class GameDashboardNewController {

    @FXML
    void handleLogout(MouseEvent event) {
        if (CentralController.getInstance().clientCore != null) {
            CentralController.getInstance().clientCore.sendLogout();
        }
        CentralController.moveTo("Login.fxml");
    }

    @FXML
    void handleOpenSettings(MouseEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.moveTo("Settings.fxml");
    }

    @FXML
    void handleOpenProfile(MouseEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.moveTo("ProfileDashboard.fxml");
    }

    @FXML
    void handleProfileMenuProfile(ActionEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.moveTo("ProfileDashboard.fxml");
    }

    @FXML
    void handleProfileMenuSettings(ActionEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.moveTo("Settings.fxml");
    }

    @FXML
    void handleProfileMenuLogout(ActionEvent event) {
        handleLogout(null);
    }

    @FXML
    void C4click(MouseEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.selectedGame = "Connect 4";
        CentralController.selectedGameId = Game.CONNECT_FOUR;
        CentralController.moveTo("chooseModeNew.fxml");
    }

    @FXML
    void TTTclicked(MouseEvent event) {
        CentralController.previousScreen = "GameDashboardNew.fxml";
        CentralController.selectedGame = "TicTacToe";
        CentralController.selectedGameId = Game.TICK_TACK_TOE;
        CentralController.moveTo("chooseModeNew.fxml");
    }
}
