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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class NewUserPage extends HBox implements Navigable {

    private CentralController rootController;

    @FXML private TextField     usernameField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label         errorLabel;
    @FXML private Button        createAccountButton;
    @FXML private Button        backButton;

    private boolean createPending = false;

    public NewUserPage(CentralController rootController) {
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
    private void handleCreateAccount(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();
        Button clickedButton = createAccountButton;
        if (clickedButton == null && event.getSource() instanceof Button sourceButton) {
            clickedButton = sourceButton;
        }

        // If any field is empty, give error message.
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            setError("All fields are required.");
            return;
        }
        // If email does not have @ as a part of the string, give error message.
        if (!email.contains("@")) {
            setError("Please enter a valid email address.");
            return;
        }
        // If password confirm input does not match password input, give error message.
        if (!password.equals(confirm)) {
            setError("Passwords do not match.");
            return;
        }

        createPending = true;
        if (clickedButton != null) {
            clickedButton.setDisable(true);
        }
        errorLabel.setStyle("-fx-text-fill: #d8dee9; -fx-font-size: 13;");
        errorLabel.setText("Creating account...");
        rootController.clientCore.setNewUserResponseHandler(this::handleCreateAccountResponse);
        rootController.clientCore.msgSend(new sysMSG("NEWU", username, password, email));

    }

    @FXML
    private void handleBack(ActionEvent event) {
        rootController.moveTo(Navigation.LOGIN);
    }

    private void handleCreateAccountResponse(sysMSG response) {
        if (!createPending || response == null || !"NEWU".equals(response.getType())) {
            return;
        }

        createPending = false;
        if (createAccountButton != null) {
            createAccountButton.setDisable(false);
        }

        if ("PASS".equals(response.getFieldA())) {
            errorLabel.setStyle("-fx-text-fill: #7CFC98; -fx-font-size: 13;");
            errorLabel.setText("Account created. Please sign in.");
            rootController.moveTo(Navigation.LOGIN);
        }
        else {
            String message = response.getFieldB();
            setError(message != null && !message.isBlank()
                    ? message
                    : "Could not create account.");
        }
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