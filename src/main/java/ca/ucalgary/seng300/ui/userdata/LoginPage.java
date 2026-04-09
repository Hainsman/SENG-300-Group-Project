package ca.ucalgary.seng300.ui.userdata;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import ca.ucalgary.seng300.ui.util.ThemeAlerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class LoginPage extends HBox implements Navigable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeBox;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button newUserButton;
    @FXML private Button forgotPasswordButton;

    public LoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ca.ucalgary.seng300.ui/" + getNavigation().getFxml()));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter your username and password.");
            return;
        }

        if (!ensureClientConnection()) {
            errorLabel.setText("Unable to connect to server. Please try again later.");
            return;
        }

        if (CentralController.getInstance().clientCore.login(username, password)) {
            CentralController.currentUserId = CentralController.getInstance().clientCore.getUserId();
            CentralController.displayName = CentralController.getInstance().clientCore.username;
            CentralController.previousScreen = null;
            CentralController.ticTacToeLocalAiMode = false;
            CentralController.connect4LocalAiMode = false;
            CentralController.moveTo(Navigation.GAME_DASHBOARD);
        } else {
            errorLabel.setText("Login failed. Please check your credentials.");
        }
    }

    // Move to NewUser screen when button is triggered.
    @FXML
    private void handleNewUser(ActionEvent event) {
        CentralController.moveTo(Navigation.NEW_USER);
    }

    private boolean ensureClientConnection() {
        if (CentralController.getInstance().clientCore == null || !CentralController.getInstance().clientCore.isConnected()) {
            CentralController.getInstance().clientCore = new ClientCore(CentralController.getInstance());
            CentralController.getInstance().clientCore.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return CentralController.getInstance().clientCore != null && CentralController.getInstance().clientCore.isConnected();
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        Alert forgotPasswordAlert = new Alert(Alert.AlertType.INFORMATION);
        forgotPasswordAlert.setTitle("Forgot Password");
        forgotPasswordAlert.setHeaderText("Need to reset your password?");
        forgotPasswordAlert.setContentText("Please contact one of our team admins to reset your password.\n\nadmin@bogosolutions.ca");
        ThemeAlerts.apply(forgotPasswordAlert);
        forgotPasswordAlert.showAndWait();
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.LOGIN;
    }

    @Override
    public void reset() {

    }
}