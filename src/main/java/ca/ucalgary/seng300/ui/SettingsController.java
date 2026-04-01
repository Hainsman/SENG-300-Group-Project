package ca.ucalgary.seng300.ui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SettingsController {

    @FXML private Label activeUsernameLabel;
    @FXML private Label statusLabel;

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private ComboBox<String> textScaleComboBox;
    @FXML private CheckBox colorBlindModeCheckBox;

    private String currentUsername = "current_user";
    private String currentEmail = "user@email.com";

    @FXML
    public void initialize() {
        textScaleComboBox.setItems(FXCollections.observableArrayList(
                "Small (90%)",
                "Normal (100%)",
                "Large (110%)",
                "Extra Large (125%)"
        ));
        textScaleComboBox.setValue(AppUiPreferences.labelFromFactor(AppUiPreferences.textScaleFactor));
        colorBlindModeCheckBox.setSelected(AppUiPreferences.colorblindMode);

        activeUsernameLabel.setText(currentUsername);
        emailField.setText(currentEmail);
    }

    @FXML
    private void handleSaveSettings() {
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String selectedTextScale = textScaleComboBox.getValue();
        boolean colorBlindModeEnabled = colorBlindModeCheckBox.isSelected();

        if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        if (!newUsername.isEmpty()) {
            currentUsername = newUsername;
            activeUsernameLabel.setText(currentUsername);
        }

        if (!newEmail.isEmpty()) {
            currentEmail = newEmail;
        }

        AppUiPreferences.textScaleFactor = AppUiPreferences.factorFromLabel(selectedTextScale);
        AppUiPreferences.colorblindMode = colorBlindModeEnabled;
        CentralController.applyUiPreferences();

        statusLabel.setText("Settings saved successfully.");
    }

    @FXML
    private void handleResetDefaults() {
        usernameField.clear();
        emailField.setText(currentEmail);
        passwordField.clear();
        confirmPasswordField.clear();
        textScaleComboBox.setValue("Normal (100%)");
        colorBlindModeCheckBox.setSelected(false);

        AppUiPreferences.textScaleFactor = 1.0;
        AppUiPreferences.colorblindMode = false;
        CentralController.applyUiPreferences();

        statusLabel.setText("Settings reset.");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (CentralController.previousScreen != null && !CentralController.previousScreen.isBlank()) {
            CentralController.moveTo(CentralController.previousScreen);
        } else {
            CentralController.moveTo("GameDashboardNew.fxml");
        }
    }
}
