package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.messages.sysMSG;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class NewUserController {

    @FXML private TextField     usernameField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button        createAccountButton;
    @FXML private Button        backButton;
    @FXML private Label         errorLabel;
    private CentralController cent;
    private boolean createPending = false;

    public void setCentralController(CentralController controller) {
        cent = controller;
        if (cent != null && cent.clientCore != null) {
            cent.clientCore.setNewUserResponseHandler(this::handleCreateAccountResponse);
        }
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();
        Button clickedButton = createAccountButton;
        if (clickedButton == null && event.getSource() instanceof Button sourceButton) {
            clickedButton = sourceButton;
        }

        // If any field is empty, give error message.
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            setError("All fields are required.");
            return;
        }
        // If email does not have @ as a part of the string, give error message.
        if (!email.contains("@")) {
            setError("Please enter a valid email address.");
            return;
        }
        // If password confirm input does not match password input, give error message.
        if (!password.equals(confirm)) {
            setError("Passwords do not match.");
            return;
        }

        if (cent == null || cent.clientCore == null || !cent.clientCore.isConnected()) {
            setError("Could not reach the platform server.");
            return;
        }

        createPending = true;
        if (clickedButton != null) {
            clickedButton.setDisable(true);
        }
        errorLabel.setStyle("-fx-text-fill: #d8dee9; -fx-font-size: 13;");
        errorLabel.setText("Creating account...");
        cent.clientCore.setNewUserResponseHandler(this::handleCreateAccountResponse);
        cent.clientCore.msgSend(new sysMSG("NEWU", username, password, email));
    }

    @FXML
    private void handleBack(ActionEvent event) {
        CentralController.moveTo("Login.fxml");
    }

    private void handleCreateAccountResponse(sysMSG response) {
        if (!createPending || response == null || !"NEWU".equals(response.getType())) {
            return;
        }

        createPending = false;
        if (createAccountButton != null) {
            createAccountButton.setDisable(false);
        }

        if ("PASS".equals(response.getFieldA())) {
            errorLabel.setStyle("-fx-text-fill: #7CFC98; -fx-font-size: 13;");
            errorLabel.setText("Account created. Please sign in.");
            CentralController.moveTo("Login.fxml");
            return;
        }

        String message = response.getFieldB();
        setError(message != null && !message.isBlank()
                ? message
                : "Could not create account.");
    }

    private void setError(String message) {
        errorLabel.setStyle("-fx-text-fill: #ff4d4d; -fx-font-size: 13;");
        errorLabel.setText(message);
    }
}
