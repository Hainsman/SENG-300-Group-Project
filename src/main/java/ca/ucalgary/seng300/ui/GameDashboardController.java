package ca.ucalgary.seng300.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameDashboardController {

    @FXML
    private Label C4Label;

    @FXML
    private Button ExitButton;

    @FXML
    private Button ProfileButton;

    @FXML
    private Button SettingsButton;

    @FXML
    private Label TTTLabel;

    @FXML
    private Label Username1Button;

    @FXML
    private Button infoButton;

    @FXML
    void handleExitButton(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void handleInfoButton(ActionEvent event) {
        CentralController.moveTo("Info.fxml");

    }



}
