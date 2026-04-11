package ca.ucalgary.seng300.ui.userdata;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.utils.Helper;
import ca.ucalgary.seng300.ui.centralcontroller.CentralController;
import ca.ucalgary.seng300.ui.util.Navigable;
import ca.ucalgary.seng300.ui.util.Navigation;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.List;

public class SettingsPage extends BorderPane implements Navigable {

    private CentralController rootController;

    @FXML private Label activeUsernameLabel;
    @FXML private Label statusLabel;

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private ComboBox<String> textScaleComboBox;
    @FXML private CheckBox colorBlindModeCheckBox;

    public SettingsPage(CentralController rootController) {
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
    public void initialize() {
        textScaleComboBox.setItems(FXCollections.observableArrayList(
                "Small (90%)",
                "Normal (100%)",
                "Large (110%)",
                "Extra Large (125%)"
        ));
        textScaleComboBox.setValue(AppUiPreferences.labelFromFactor(AppUiPreferences.textScaleFactor));
        colorBlindModeCheckBox.setSelected(AppUiPreferences.colorblindMode);

        reloadAccountFieldsFromStore();
        if (statusLabel != null) {
            statusLabel.setText("Edit your account and preferences, then save.");
        }
    }

    private User currentUserOrNull() {
        int id = CentralController.currentUserId;
        if (id == 0) {
            return null;
        }
        return UserStore.getInstance().get(id);
    }

    private void reloadAccountFieldsFromStore() {
        User u = currentUserOrNull();
        if (u == null) {
            if (activeUsernameLabel != null) {
                activeUsernameLabel.setText("—");
            }
            if (usernameField != null) {
                usernameField.setText("");
            }
            if (emailField != null) {
                emailField.setText("");
            }
            return;
        }
        if (activeUsernameLabel != null) {
            activeUsernameLabel.setText(u.getlogin());
        }
        if (usernameField != null) {
            usernameField.setText(u.getlogin());
        }
        if (emailField != null) {
            emailField.setText(u.getEmail() != null ? u.getEmail() : "");
        }
        if (passwordField != null) {
            passwordField.clear();
        }
        if (confirmPasswordField != null) {
            confirmPasswordField.clear();
        }
    }

    @FXML
    private void handleSaveSettings() {
        User u = currentUserOrNull();
        if (u == null) {
            if (statusLabel != null) {
                statusLabel.setText("You must be logged in to save account settings.");
            }
            return;
        }

        String newUsername = usernameField != null ? usernameField.getText().trim() : "";
        String newEmail = emailField != null ? emailField.getText().trim() : "";
        String newPassword = passwordField != null ? passwordField.getText() : "";
        String confirmPassword = confirmPasswordField != null ? confirmPasswordField.getText() : "";
        String selectedTextScale = textScaleComboBox != null ? textScaleComboBox.getValue() : "Normal (100%)";
        boolean colorBlindModeEnabled = colorBlindModeCheckBox != null && colorBlindModeCheckBox.isSelected();

        if (newUsername.isEmpty()) {
            if (statusLabel != null) {
                statusLabel.setText("Username cannot be empty.");
            }
            return;
        }
        if (newEmail.isEmpty()) {
            if (statusLabel != null) {
                statusLabel.setText("Email cannot be empty.");
            }
            return;
        }
        if (!Helper.isEmailValid(newEmail, Constants.EMAIL_REGEX)) {
            if (statusLabel != null) {
                statusLabel.setText("Please enter a valid email address.");
            }
            return;
        }

        UserStore store = UserStore.getInstance();
        if (!newUsername.equals(u.getlogin())) {
            List<User> takenLogin = store.findByUserName(newUsername);
            if (!takenLogin.isEmpty() && takenLogin.getFirst().getID() != u.getID()) {
                if (statusLabel != null) {
                    statusLabel.setText("That username is already taken.");
                }
                return;
            }
        }
        List<User> takenEmail = store.findByUserEmail(newEmail);
        if (!takenEmail.isEmpty() && takenEmail.getFirst().getID() != u.getID()) {
            if (statusLabel != null) {
                statusLabel.setText("That email is already registered to another account.");
            }
            return;
        }

        if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            if (statusLabel != null) {
                statusLabel.setText("Passwords do not match.");
            }
            return;
        }

        u.setlogin(newUsername);
        u.setEmail(newEmail);
        if (!newPassword.isEmpty()) {
            u.setPasswordHash(Helper.hashPassword(newPassword, Constants.HASH_ALGORITHM));
            if (passwordField != null) {
                passwordField.clear();
            }
            if (confirmPasswordField != null) {
                confirmPasswordField.clear();
            }
        }

        store.update(u);
        CentralController.displayName = u.getlogin();
        if (activeUsernameLabel != null) {
            activeUsernameLabel.setText(u.getlogin());
        }

        AppUiPreferences.textScaleFactor = AppUiPreferences.factorFromLabel(selectedTextScale);
        AppUiPreferences.colorblindMode = colorBlindModeEnabled;
        rootController.applyUiPreferences();

        if (statusLabel != null) {
            statusLabel.setText("Settings saved successfully.");
        }
    }

    @FXML
    private void handleResetDefaults() {
        AppUiPreferences.textScaleFactor = 1.0;
        AppUiPreferences.colorblindMode = false;
        if (textScaleComboBox != null) {
            textScaleComboBox.setValue("Normal (100%)");
        }
        if (colorBlindModeCheckBox != null) {
            colorBlindModeCheckBox.setSelected(false);
        }
        rootController.applyUiPreferences();

        reloadAccountFieldsFromStore();
        if (statusLabel != null) {
            statusLabel.setText("Accessibility reset to defaults; account fields reloaded from disk.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (rootController.previousScreen != null) {
            rootController.moveTo(rootController.previousScreen);
        } else {
            rootController.moveTo(Navigation.GAME_DASHBOARD);
        }
    }

    @Override
    public Navigation getNavigation() {
        return Navigation.SETTINGS;
    }

    @Override
    public void reset() {
        initialize();
    }
}
