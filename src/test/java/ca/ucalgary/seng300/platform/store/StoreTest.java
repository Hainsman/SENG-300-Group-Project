package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

/**
 * Tests for the generic Store<T> persistence layer.
 *
 * Store<T> is package-private, so we test it through UserStore — a public
 * concrete subclass that extends Store<User>. Every method tested here
 * (create, get, find, update, delete) lives entirely in Store<T>;
 * UserStore only adds findByUserName() and findByUserEmail() on top.
 *
 * Why UserStore?
 *   - Its constructor accepts a file path, letting us point it at a
 *     @TempDir so tests never touch production data files.
 *   - User is the simplest concrete Record subclass with meaningful fields.
 *
 * Test file is in the same package (ca.ucalgary.seng300.platform.store)
 * so that the protected find() method is accessible directly on the store.
 */
class StoreTest {

    /**
     * JUnit 5 creates a fresh temporary directory for each test method
     * and deletes it automatically afterward.  Pointing the store at a
     * file inside this directory guarantees:
     *   - tests are isolated (no shared state between methods)
     *   - the real datafiles/ folder is never touched
     */
    @TempDir
    Path tempDir;

    /** The store instance under test, re-created fresh before every test. */
    private UserStore store;

    /**
     * Resolves a unique "users.txt" inside the temp directory and
     * constructs a fresh UserStore before each test.
     *
     * Because the file does not exist yet, Store.loadAll() catches the
     * FileNotFoundException and starts with an empty map — which is
     * exactly the blank-slate we want for every test.
     */
    @BeforeEach
    void setUp() {
        String filePath = tempDir.resolve("users.txt").toString();
        store = new UserStore(filePath);
    }

    // ---------------------------------------------------------------
    // Tests will be added in subsequent commits:
    //   Step 2 — create() and get()
    //   Step 3 — find() (match, no match, multiple)
    //   Step 4 — update()
    //   Step 5 — delete()
    //   Step 6 — persistence (data survives a new Store instance)
    //   Step 7 — edge cases (missing ID, empty store)
    // ---------------------------------------------------------------
}
