package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

//import java.util.UUID;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeBox;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button newUserButton;
    @FXML private Button forgotPasswordButton;
    private static LoginController loginController;

    private CentralController root;
    private ClientCore clientCore = null;
    public void setRoot(CentralController c){
        root = c;
        clientCore = root.getCC();
    }

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
        /*
        UUID userSession = UUID.randomUUID();

        Authenticate auth = new Authenticate(userSession, username, password);
        //var clientCore = ClientCoreV2.getInstance();
        //clientCore.msgSend(auth);
        //ServerResponse resp = clientCore.msgRecieve();
        if(resp.status.equals(ServerResponse.Status.FAIL)){
            errorLabel.setText(resp.details[0]);
            return;
        }*/
        clientCore.msgSend(new sysMSG("AUTH", username, password));

        //root.moveTo("GameDashboard.fxml");
    }
    public void loginReply(sysMSG s){
        switch (s.getFieldA()){
            case "PASS":
                CentralController.moveTo("GameDashboard.fxml");
                break;
            case "NF":
                errorLabel.setText("Error Username not found");
                break;
            default:
                errorLabel.setText("Error username or password incorrect");
        }
    }

    // Move to NewUser screen when button is triggered.
    @FXML
    private void handleNewUser(ActionEvent event) {CentralController.moveTo("NewUser.fxml");}

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        Alert forgotPasswordAlert = new Alert(Alert.AlertType.INFORMATION);
        forgotPasswordAlert.setTitle("Forgot Password");
        forgotPasswordAlert.setHeaderText("Need to reset your password?");
        forgotPasswordAlert.setContentText("Please contact one of our team admins to reset your password.\n\nadmin@bogosolutions.ca");
        forgotPasswordAlert.showAndWait();
    }


}