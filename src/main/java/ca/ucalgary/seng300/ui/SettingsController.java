package ca.ucalgary.seng300.ui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class SettingsController {

    @FXML private Label activeUsernameLabel;
    @FXML private Label statusLabel;

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private ComboBox<String> textScaleComboBox;
    @FXML private CheckBox colorBlindModeCheckBox;

    // Example stored current user values
    private String currentUsername = "current_user";
    private String currentEmail = "user@email.com";

    @FXML
    public void initialize() {
        activeUsernameLabel.setText(currentUsername);
        emailField.setText(currentEmail);
        textScaleComboBox.setValue("Normal (100%)");
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

        applyTextScaling(selectedTextScale);
        applyColorBlindMode(colorBlindModeEnabled);

        // Save Values here
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

        applyTextScaling("Normal (100%)");
        applyColorBlindMode(false);

        statusLabel.setText("Settings reset.");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // return to previous menu
        statusLabel.setText("Returning to previous screen...");
    }

    @FXML
    private void handleMinimize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void applyTextScaling(String selectedScale) {
        Scene scene = activeUsernameLabel.getScene();
        if (scene == null || selectedScale == null) {
            return;
        }

        String textStylesheet = getClass()
                .getResource("/stylesheets/normalText.css")
                .toExternalForm();


        switch (selectedScale) {
            case "Small (90%)":
                textStylesheet = getClass()
                        .getResource("/stylesheets/smallText.css")
                        .toExternalForm();
                break;
            case "Normal (100%)":
                textStylesheet = getClass()
                        .getResource("/stylesheets/normalText.css")
                        .toExternalForm();
                break;
            case "Large (110%)":
                textStylesheet = getClass()
                        .getResource("/stylesheets/largeText.css")
                        .toExternalForm();
                break;
            case "Extra Large (125%)":
                textStylesheet = getClass()
                        .getResource("/stylesheets/extraLargeText.css")
                        .toExternalForm();
                break;
        }
        if(!scene.getStylesheets().contains(textStylesheet)) {
            scene.getStylesheets().add(textStylesheet);
        }
    }

    private void applyColorBlindMode(boolean enabled) {
        Scene scene = activeUsernameLabel.getScene();
        if (scene == null) {
            return;
        }

        String colorblindStylesheet = getClass()
                .getResource("/stylesheets/colorblind.css")
                .toExternalForm();

        if (enabled) {
            if (!scene.getStylesheets().contains(colorblindStylesheet)) {
                scene.getStylesheets().add(colorblindStylesheet);
            }
        } else {
            scene.getStylesheets().remove(colorblindStylesheet);
        }
    }
}