package ca.ucalgary.seng300.platform.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke test for the login UI class location.
 *
 * The old LoginController class was removed in favour of LoginPage.
 */
class LoginControllerTest {

    @Test
    void loginPage_classExists() throws Exception {
        Class<?> loginPageClass = Class.forName("ca.ucalgary.seng300.ui.userdata.LoginPage");
        assertNotNull(loginPageClass);
    }
}