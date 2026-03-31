package ca.ucalgary.seng300.platform.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User domain object.
 *
 * User is a pure in-memory value object — no I/O, no dependencies —
 * so these tests need nothing beyond a plain JUnit 5 setup.
 *
 * Notable: one test deliberately exposes a known bug in setPasswordHash()
 * where the implementation writes to this.login instead of this.passwordHash.
 */
class UserDomainTest {

    private User user;

    /**
     * Creates a fresh User before each test so mutations in one test
     * cannot affect another.
     */
    @BeforeEach
    void setUp() {
        user = new User(1, "alice", "alice@example.com", "secret");
    }

    /**
     * All four fields passed to the constructor must be stored correctly.
     */
    @Test
    void constructor_setsAllFieldsCorrectly() {
        assertEquals(1,                   user.getID());
        assertEquals("alice",             user.getlogin());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("secret",            user.getPasswordHash());
    }

    /**
     * isActive must default to true on construction — a newly created
     * user account is active by default.
     */
    @Test
    void constructor_isActiveDefaultsToTrue() {
        assertTrue(user.getIsActive());
    }


    /**
     * setlogin() must update the login field and getlogin() must
     * reflect the new value.
     */
    @Test
    void setlogin_updatesLoginField() {
        user.setlogin("bob");
        assertEquals("bob", user.getlogin());
    }


    /**
     * setEmail() must update the email field and getEmail() must
     * reflect the new value.
     */
    @Test
    void setEmail_updatesEmailField() {
        user.setEmail("bob@example.com");
        assertEquals("bob@example.com", user.getEmail());
    }

    /**
     * setIsActive(false) must flip the flag; a second call with true
     * must flip it back. Validates both directions.
     */
    @Test
    void setIsActive_togglesTheFlag() {
        user.setIsActive(false);
        assertFalse(user.getIsActive());

        user.setIsActive(true);
        assertTrue(user.getIsActive());
    }

    // ---------------------------------------------------------------
    // setPasswordHash / getPasswordHash  — KNOWN BUG
    //
    // The current implementation of setPasswordHash() reads:
    //     this.login = passwordHash;
    // instead of:
    //     this.passwordHash = passwordHash;
    //
    // The two tests below expose both symptoms of this bug:
    //   1. getPasswordHash() still returns the old value after the call
    //   2. getlogin() is silently overwritten with the new hash value
    // ---------------------------------------------------------------

    /**
     * After calling setPasswordHash(), getPasswordHash() should return
     * the new hash.
     *
     * FAILS with the current implementation because setPasswordHash()
     * writes to this.login instead of this.passwordHash, leaving
     * passwordHash unchanged.
     */
    @Test
    void setPasswordHash_updatesPasswordHashField() {
        user.setPasswordHash("newHash");
        assertEquals("newHash", user.getPasswordHash(),
                "setPasswordHash() should update passwordHash, not login");
    }

    /**
     * Calling setPasswordHash() must not modify the login field.
     *
     * FAILS with the current implementation because setPasswordHash()
     * overwrites this.login with the hash value as a side effect.
     */
    @Test
    void setPasswordHash_doesNotCorruptLoginField() {
        user.setPasswordHash("newHash");
        assertEquals("alice", user.getlogin(),
                "setPasswordHash() must not overwrite the login field");
    }
}
