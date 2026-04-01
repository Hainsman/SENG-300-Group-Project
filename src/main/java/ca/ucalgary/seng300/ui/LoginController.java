package ca.ucalgary.seng300.ui;

// import ca.ucalgary.seng300.platform.messages.Authenticate;
// import ca.ucalgary.seng300.platform.server.ServerCoreMultiUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.UUID;

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

        UUID userSession = UUID.randomUUID();

        // Authenticate auth = new Authenticate(userSession, username, password);
        //TODO make client core work

        // TODO: pass to backend auth service when user attempts to login
        // boolean success = authService.login(username, password, rememberMeBox.isSelected());

        int uid = username.hashCode();
        CentralController.currentUserId = uid == Integer.MIN_VALUE ? 1 : Math.abs(uid);
        CentralController.displayName = username;
        CentralController.previousScreen = null;
        CentralController.moveTo("GameDashboardNew.fxml");
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