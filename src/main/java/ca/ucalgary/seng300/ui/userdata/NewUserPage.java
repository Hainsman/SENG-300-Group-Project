package ca.ucalgary.seng300.ui.userdata;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.utils.Helper;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class NewUserPage extends HBox implements Navigable {

    @FXML private TextField     usernameField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label         errorLabel;

    public CentralController cent;

    public NewUserPage(CentralController c) {
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
        System.out.println("newuser sent");
        cent.clientCore.msgSend(new sysMSG("NEWU", username, password, email));
    }

    public void newReply(sysMSG s){
        if(s.getFieldA().equals("PASS")){
            CentralController.moveTo(Navigation.LOGIN);
        }else{
            errorLabel.setText(s.getFieldB());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        CentralController.moveTo(Navigation.LOGIN);
    }

    private void setError(String message) {
        errorLabel.setStyle("-fx-text-fill: #ff4d4d; -fx-font-size: 13;");
        errorLabel.setText(message);
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.NEW_USER;
    }

    @Override
    public void reset() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}