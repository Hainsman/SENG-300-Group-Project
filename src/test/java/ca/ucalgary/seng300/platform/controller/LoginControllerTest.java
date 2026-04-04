package ca.ucalgary.seng300.platform.controller;

import ca.ucalgary.seng300.ui.LoginController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LoginController.
 *
 * LoginController's logic is entirely in @FXML private handlers that require
 * a live JavaFX environment to invoke. Until a JavaFX test harness (e.g. TestFX)
 * is added to the project, only class-level smoke tests are feasible here.
 */
class LoginControllerTest {

    @Test
    void loginController_classExists() {
        LoginController controller = new LoginController();
        assertNotNull(controller);
    }
}