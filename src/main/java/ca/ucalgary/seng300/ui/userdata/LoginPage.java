package ca.ucalgary.seng300.ui.userdata;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.utils.Helper;
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
import java.util.List;

public class LoginPage extends HBox implements Navigable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeBox;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button newUserButton;
    @FXML private Button forgotPasswordButton;

    public CentralController cent;

    public LoginPage(CentralController c) {
        try {
            cent = c;
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
        System.out.println("loginsent");
        cent.clientCore.msgSend(new sysMSG("AUTH", username, password));

    }

    public void loginReply(sysMSG s){
        if(s.getFieldA().equals("PASS")){
            int i = -1;
            try{
                i = Integer.parseInt(s.getFieldB());
            }catch(Exception ignore){}
            //CentralController.currentUserId = user.getID();
            //CentralController.displayName = user.getlogin();
            CentralController.previousScreen = null;
            CentralController.localAiMode = false;
            CentralController.moveTo(Navigation.GAME_DASHBOARD);
            errorLabel.setText(s.getFieldB());
        }
    }

    // Move to NewUser screen when button is triggered.
    @FXML
    private void handleNewUser(ActionEvent event) {
        CentralController.moveTo(Navigation.NEW_USER);
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