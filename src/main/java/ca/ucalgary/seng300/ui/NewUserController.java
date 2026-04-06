package ca.ucalgary.seng300.ui;

import ca.ucalgary.seng300.platform.client.ClientCore;
import ca.ucalgary.seng300.platform.client.ClientCoreV2;
import ca.ucalgary.seng300.platform.messages.CreateAccount;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.serverreponse.ServerResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.UUID;

public class NewUserController {

    @FXML private TextField     usernameField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label         errorLabel;
    private static NewUserController newUserController;

    public static NewUserController getInstance(){
        if (newUserController == null)
            newUserController = new NewUserController();
        return newUserController;
    }

    private CentralController root;
    private ClientCore clientCore = null;
    public void setRoot(CentralController c){
        root = c;
        clientCore = root.getCC();
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();

        // If any field is empty, give error message.
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            setError("All fields are required.");
            return;
        }
        // If password confirm input does not match password input, give error message.
        if (!password.equals(confirm)) {
            setError("Passwords do not match.");
            return;
        }

        clientCore.msgSend(new sysMSG("NEWU", username, email, password));
        /*
        UUID userSession = UUID.randomUUID();
        // TODO: wire to backend when New User is created successfully
        //CreateAccount createAccount = new CreateAccount(userSession, password, "daniel.maximov@ucalgary.ca", username); //TODO UI please make a email text field and pass parameters here
        var clientCore = ClientCoreV2.getInstance();
        clientCore.msgSend(createAccount);
        ServerResponse resp = clientCore.msgRecieve();
        if(resp.status.equals(ServerResponse.Status.FAIL)){
            errorLabel.setText(resp.details[0]);
            return;
        }
        CentralController.moveTo("GameDashboard.fxml");


        // Navigate back to login on success
        CentralController.moveTo("Login.fxml");*/
    }

    public void newUserReply(sysMSG s){
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

    @FXML
    private void handleBack(ActionEvent event) {
        CentralController.moveTo("Login.fxml");
    }

    private void setError(String message) {
        errorLabel.setStyle("-fx-text-fill: #ff4d4d; -fx-font-size: 13;");
        errorLabel.setText(message);
    }
}