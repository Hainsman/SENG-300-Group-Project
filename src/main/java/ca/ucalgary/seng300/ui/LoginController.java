package ca.ucalgary.seng300.ui;

// import ca.ucalgary.seng300.platform.messages.Authenticate;
// import ca.ucalgary.seng300.platform.server.ServerCoreMultiUser;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button newUserButton;
    @FXML private Button forgotPasswordButton;
    public CentralController cent;
    private boolean loginPending = false;

    public void setCentralController(CentralController controller) {
        cent = controller;
        if (cent != null && cent.clientCore != null) {
            cent.clientCore.setAuthResponseHandler(this::handleAuthResponse);
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        Button clickedButton = loginButton;
        if (clickedButton == null && event.getSource() instanceof Button sourceButton) {
            clickedButton = sourceButton;
        }

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter your email/username and password.");
            return;
        }

        if (cent == null || cent.clientCore == null || !cent.clientCore.isConnected()) {
            errorLabel.setText("Could not reach the platform server.");
            return;
        }

        loginPending = true;
        if (clickedButton != null) {
            clickedButton.setDisable(true);
        }
        errorLabel.setText("Signing in...");
        cent.clientCore.setAuthResponseHandler(this::handleAuthResponse);
        cent.clientCore.msgSend(new sysMSG("AUTH", username, password));
    }

    private void handleAuthResponse(sysMSG response) {
        if (!loginPending || response == null || !"AUTH".equals(response.getType())) {
            return;
        }

        loginPending = false;
        if (loginButton != null) {
            loginButton.setDisable(false);
        }

        if ("PASS".equals(response.getFieldA())) {
            errorLabel.setText("");
            CentralController.previousScreen = null;
            CentralController.moveTo("GameDashboardNew.fxml");
            return;
        }

        String message = response.getFieldB();
        errorLabel.setText(message != null && !message.isBlank()
                ? message
                : "Login failed.");
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
