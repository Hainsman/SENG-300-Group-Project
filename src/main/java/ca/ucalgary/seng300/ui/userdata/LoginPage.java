package ca.ucalgary.seng300.ui.userdata;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.utils.Helper;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.SoundEffect;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import ca.ucalgary.seng300.ui.Popup;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.List;

public class LoginPage extends HBox implements Navigable {

    private CentralController rootController;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeBox;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button newUserButton;
    @FXML private Button forgotPasswordButton;

    private boolean loginPending = false;

    public LoginPage(CentralController rootController) {
        try {
            this.rootController = rootController;

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
        Button clickedButton = loginButton;
        if (clickedButton == null && event.getSource() instanceof Button sourceButton) {
            clickedButton = sourceButton;
        }

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter your email/username and password.");
            return;
        }
        System.out.println("loginsent");
        rootController.clientCore.msgSend(new sysMSG("AUTH", username, password));

        loginPending = true;
        if (clickedButton != null) {
            clickedButton.setDisable(true);
        }
        errorLabel.setText("Signing in...");
        rootController.clientCore.msgSend(new sysMSG("AUTH", username, password));
    }

    public void loginReply(sysMSG s){
        if(s.getFieldA().equals("PASS")){
            loginPending = false;
            loginButton.setDisable(false);
            errorLabel.setText("");
            rootController.getWindowManager().playAudioClip(SoundEffect.USER_LOGGED_IN);
            rootController.moveTo(Navigation.GAME_DASHBOARD);
            rootController.previousScreen = null;
            errorLabel.setText(s.getFieldB());
        }
        else{
            String message = s.getFieldB();
            errorLabel.setText(message != null && !message.isBlank()
                    ? message
                    : "Login failed.");
        }
    }

    // Move to NewUser screen when button is triggered.
    @FXML
    private void handleNewUser(ActionEvent event) {
        rootController.moveTo(Navigation.NEW_USER);
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        Popup.showInfo(getScene().getWindow(),
                "Need to reset your password?\n\nPlease contact one of our team admins to reset your password.\n\nadmin@bogosolutions.ca");
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.LOGIN;
    }

    @Override
    public void reset() {
        errorLabel.setText("");
    }
}