package ca.ucalgary.seng300.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SettingsController {
    @FXML private TextField displayNameField;
    @FXML private TextField emailField;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        if (displayNameField != null) {
            displayNameField.setText("Player");
        }
        if (emailField != null) {
            emailField.setText("player@example.com");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (statusLabel != null) {
            statusLabel.setText("Profile updated.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (CentralController.previousScreen != null && !CentralController.previousScreen.isBlank()) {
            CentralController.moveTo(CentralController.previousScreen);
            return;
        }
        CentralController.moveTo("GameDashboardNew.fxml");
    }
}
