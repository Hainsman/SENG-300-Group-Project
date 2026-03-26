package ca.ucalgary.seng300.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
        Alert forgotPasswordAlert = new Alert(Alert.AlertType.INFORMATION);
        forgotPasswordAlert.setTitle("Forgot Password");
        forgotPasswordAlert.setHeaderText("Need to reset your password?");
        forgotPasswordAlert.setContentText("Please contact one of our team admins to reset your password.\n\nadmin@bogosolutions.ca");
        forgotPasswordAlert.showAndWait();
    }
}