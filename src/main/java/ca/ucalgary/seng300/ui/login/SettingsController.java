package ca.ucalgary.seng300.ui.login;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.utils.Helper;
import ca.ucalgary.seng300.ui.centralcontroller.CentralController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SettingsController {
    @FXML private TextField displayNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> textSizeComboBox;
    @FXML private CheckBox colorBlindCheckBox;
    @FXML private Label statusLabel;
    private CentralController cent;
    private String pendingTextSizePreference;
    private boolean pendingColorBlindMode;

    public void setCentralController(CentralController controller) {
        cent = controller;
        if (cent != null && cent.clientCore != null) {
            cent.clientCore.setProfileUpdateResponseHandler(this::handleProfileUpdateResponse);
            // Pre-fill fields from clientCore cache if available
            if (cent.clientCore.username != null) {
                displayNameField.setText(cent.clientCore.username);
            }
            if (cent.clientCore.email != null) {
                emailField.setText(cent.clientCore.email);
            }
        }
    }

    @FXML
    private void initialize() {
        // Initial defaults
        displayNameField.setText("");
        emailField.setText("");
        textSizeComboBox.getItems().setAll("Normal", "Large", "Extra Large");
        textSizeComboBox.setValue(labelForTextSize(CentralController.getTextSizePreference()));
        colorBlindCheckBox.setSelected(CentralController.isColorBlindModeEnabled());
        if (passwordField != null) {
            passwordField.clear();
        }
        if (confirmPasswordField != null) {
            confirmPasswordField.clear();
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        String newLogin = displayNameField.getText().trim();
        String newEmail = emailField.getText().trim();

        String newPassword = passwordField != null ? passwordField.getText() : "";
        String confirmPassword = confirmPasswordField != null ? confirmPasswordField.getText() : "";
        String textSizePreference = preferenceForLabel(textSizeComboBox.getValue());
        boolean colorBlindMode = colorBlindCheckBox.isSelected();

        if (newLogin.isEmpty() || newEmail.isEmpty()) {
            statusLabel.setText("Username and email cannot be empty.");
            statusLabel.setStyle("-fx-text-fill: #ff4d4d;");
            return;
        }
        if (!Helper.isEmailValid(newEmail, Constants.EMAIL_REGEX)) {
            statusLabel.setText("Please enter a valid email address.");
            statusLabel.setStyle("-fx-text-fill: #ff4d4d;");
            return;
        }
        if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            statusLabel.setStyle("-fx-text-fill: #ff4d4d;");
            return;
        }

        if (cent == null || cent.clientCore == null || !cent.clientCore.isConnected()) {
            statusLabel.setText("Not connected to server.");
            statusLabel.setStyle("-fx-text-fill: #ff4d4d;");
            return;
        }

        statusLabel.setText("Updating profile...");
        statusLabel.setStyle("-fx-text-fill: #d8dee9;");
        pendingTextSizePreference = textSizePreference;
        pendingColorBlindMode = colorBlindMode;
        String passwordHash = newPassword.isEmpty()
                ? ""
                : Helper.hashPassword(newPassword, Constants.HASH_ALGORITHM);
        cent.clientCore.msgSend(new sysMSG("UPDATE_PROFILE", newLogin, newEmail, passwordHash));
    }

    private void handleProfileUpdateResponse(sysMSG response) {
        if (response == null || !"UPDATE_PROFILE".equals(response.getType())) {
            return;
        }

        if ("PASS".equals(response.getFieldA())) {
            CentralController.updateAccessibilityPreferences(pendingTextSizePreference, pendingColorBlindMode);
            // Update local cache if necessary
            if (cent != null && cent.clientCore != null) {
                cent.clientCore.username = displayNameField.getText().trim();
                cent.clientCore.email = emailField.getText().trim();
                // If a new password was provided and accepted, the server returns the hash in the payload
                if (response.getPayload() != null && !response.getPayload().isEmpty()) {
                    // Assuming clientCore has a way to store the hash if needed, 
                    // or at least we know the update was successful.
                    // If there was a 'password' field in ClientCore we would update it here.
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Profile updated successfully");
            alert.showAndWait();

            statusLabel.setText("Profile updated successfully");
            statusLabel.setStyle("-fx-text-fill: #7CFC98;");
            if (passwordField != null) {
                passwordField.clear();
            }
            if (confirmPasswordField != null) {
                confirmPasswordField.clear();
            }
        } else {
            String reason = response.getFieldB() != null ? response.getFieldB() : "Unknown error";
            statusLabel.setText("Failed: " + reason);
            statusLabel.setStyle("-fx-text-fill: #ff4d4d;");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Failed");
            alert.setHeaderText("Profile Update Failed");
            alert.setContentText("Reason: " + reason + "\n\nAn automated notification has been sent to your registered email (Mock).");
            alert.showAndWait();
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

    private String preferenceForLabel(String label) {
        if (label == null) {
            return "normal";
        }
        return switch (label.toLowerCase()) {
            case "large" -> "large";
            case "extra large" -> "xlarge";
            default -> "normal";
        };
    }

    private String labelForTextSize(String preference) {
        if (preference == null) {
            return "Normal";
        }
        return switch (preference.toLowerCase()) {
            case "large" -> "Large";
            case "xlarge" -> "Extra Large";
            default -> "Normal";
        };
    }
}
