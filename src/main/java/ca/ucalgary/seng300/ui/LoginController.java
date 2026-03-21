package ca.ucalgary.seng300.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeBox;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button newUserButton;
    @FXML private Button forgotPasswordButton;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter your username and password.");
            return;
        }

        // TODO: pass to backend auth service when user attempts to login
        // boolean success = authService.login(username, password, rememberMeBox.isSelected());

        // Placeholder: navigate to game screen on success
        CentralController.moveTo("GameGUI.fxml");
    }

    // Move to NewUser screen when button is triggered.
    @FXML
    private void handleNewUser(ActionEvent event) {
        CentralController.moveTo("NewUser.fxml");
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        // TODO: navigate to forgot password screen when built
        CentralController.moveTo("ForgotPassword.fxml");
    }
}