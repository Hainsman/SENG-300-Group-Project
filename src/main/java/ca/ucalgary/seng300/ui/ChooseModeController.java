package ca.ucalgary.seng300.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ChooseModeController {

    @FXML
    private Label gameTitleLabel;

    @FXML
    public void initialize() {

        gameTitleLabel.setText(CentralController.selectedGame);

    }

    @FXML
    void handleHostGame(MouseEvent event) {
        CentralController.moveTo("hostLobby.fxml");
    }

    @FXML
    void handleJoinGame(MouseEvent event) {
        CentralController.moveTo("joinLobby.fxml");
    }

    @FXML
    void handleOpenSettingsEssam(MouseEvent event) {
        CentralController.previousScreen = "chooseModeNew.fxml";
        CentralController.moveTo("Settings_Essam.fxml");
    }

    @FXML
    void handleBack(MouseEvent event) {
        CentralController.moveTo("GameDashboardNew.fxml");
    }
}
