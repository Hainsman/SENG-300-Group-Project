package ca.ucalgary.seng300.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class NewUserController {

    @FXML private TextField     usernameField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label         errorLabel;
    private static NewUserController newUserController;

    public static NewUserController getInstance(){
        if (newUserController == null)
            newUserController = new NewUserController();
        return newUserController;
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();

        // If any field is empty, give error message.
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            setError("All fields are required.");
            return;
        }
        // If password confirm input does not match password input, give error message.
        if (!password.equals(confirm)) {
            setError("Passwords do not match.");
            return;
        }
        
        // Navigate back to login on success
        CentralController.moveTo("Login.fxml");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        CentralController.moveTo("Login.fxml");
    }

    private void setError(String message) {
        errorLabel.setStyle("-fx-text-fill: #ff4d4d; -fx-font-size: 13;");
        errorLabel.setText(message);
    }
}