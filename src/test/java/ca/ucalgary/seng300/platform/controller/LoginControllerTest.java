package ca.ucalgary.seng300.platform.controller;

import ca.ucalgary.seng300.ui.login.LoginController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        loginController = new LoginController();
    }

    @Test
    void controllerCanBeInstantiated() {
        assertNotNull(loginController);
    }

    @Test
    void setCentralControllerAcceptsNull() {
        assertDoesNotThrow(() -> loginController.setCentralController(null));
    }
}