package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.client.ClientCoreV2;
import ca.ucalgary.seng300.platform.messages.Authenticate;
import ca.ucalgary.seng300.platform.messages.CreateAccount;
import ca.ucalgary.seng300.platform.server.ServerCoreMultiUser;
import ca.ucalgary.seng300.platform.serverreponse.ServerResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.UUID;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeBox;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button newUserButton;
    @FXML private Button forgotPasswordButton;
    private static LoginController loginController;

    public static LoginController getInstance(){
        if (loginController == null)
            loginController = new LoginController();
        return loginController;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter your username and password.");
            return;
        }

        UUID userSession = UUID.randomUUID();

        Authenticate auth = new Authenticate(userSession, username, password);
        var clientCore = ClientCoreV2.getInstance();
        clientCore.msgSend(auth);
        ServerResponse resp = clientCore.msgRecieve();
        if(resp.status.equals(ServerResponse.Status.FAIL)){
            errorLabel.setText(resp.details[0]);
            return;
        }
        CentralController.moveTo("GameDashboard.fxml");
    }

    // Move to NewUser screen when button is triggered.
    @FXML
    private void handleNewUser(ActionEvent event) {
//        UUID userSession = UUID.randomUUID();
//        CentralController.moveTo("NewUser.fxml");
//        String username = usernameField.getText().trim();
//        String password = passwordField.getText();
//        if (username.isEmpty() || password.isEmpty()) {
//            errorLabel.setText("Please Fill all fields.");
//            return;
//        }
//
//        CreateAccount createAccount = new CreateAccount(userSession, password, "daniel.maximov@ucalgary.ca", username); //TODO UI please make a email text field and pass parameters here
//
//        var clientCore = ClientCoreV2.getInstance();
//        clientCore.msgSend(createAccount);
//        ServerResponse resp = clientCore.msgRecieve();
//        if(resp.status.equals(ServerResponse.Status.FAIL)){
//            errorLabel.setText(resp.details[0]);
//            return;
//        }
        CentralController.moveTo("NewUser.fmxl");
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