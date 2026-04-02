package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.utils.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

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

        UserStore store = UserStore.getInstance();
        List<User> matches = store.findByUserName(username);
        if (matches.isEmpty()) {
            errorLabel.setText("Username not found.");
            return;
        }
        User user = matches.getFirst();
        String hash = Helper.hashPassword(password, Constants.HASH_ALGORITHM);
        if (!hash.equals(user.getPasswordHash())) {
            errorLabel.setText("Incorrect password.");
            return;
        }
        if (!user.getIsActive()) {
            errorLabel.setText("This account is disabled.");
            return;
        }

        CentralController.currentUserId = user.getID();
        CentralController.displayName = user.getlogin();
        CentralController.previousScreen = null;
        CentralController.ticTacToeLocalAiMode = false;
        CentralController.connect4LocalAiMode = false;
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