package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
     * and deletes it automatically afterward. Pointing the store at a
     * file inside this directory guarantees:
     *   - tests are isolated (no shared state between methods)
     *   - the real datafiles/ folder is never touched
     */
    @TempDir
    Path tempDir;

    /** The store instance under test, re-created fresh before every test. */
    private UserStore store;

    /**
     * Resolves "users.txt" inside the temp directory and constructs a
     * fresh UserStore before each test. Because the file does not exist
     * yet, Store.loadAll() catches the FileNotFoundException and starts
     * with an empty map — the blank slate we want for every test.
     */
    @BeforeEach
    void setUp() {
        String filePath = tempDir.resolve("users.txt").toString();
        store = new UserStore(filePath);
    }

    /**
     * A record inserted via create() must be retrievable by its ID.
     * Basic write-then-read round-trip.
     */
    @Test
    void create_thenGet_returnsTheSameRecord() {
        User user = new User(1, "alice", "alice@example.com", "hash123");

        store.create(user);

        User result = store.get(1);
        assertNotNull(result, "get() should return the record that was just created");
        assertEquals("alice", result.getlogin());
        assertEquals("alice@example.com", result.getEmail());
    }

    /**
     * Two records with different IDs must be stored independently.
     * get() should return the correct record for each ID.
     */
    @Test
    void create_multipleRecords_eachRetrievableByItsOwnId() {
        User alice = new User(1, "alice", "alice@example.com", "hashA");
        User bob   = new User(2, "bob",   "bob@example.com",   "hashB");

        store.create(alice);
        store.create(bob);

        assertEquals("alice", store.get(1).getlogin());
        assertEquals("bob",   store.get(2).getlogin());
    }

    /**
     * Creating a record with a duplicate ID overwrites the existing one.
     * Store.create() calls ConcurrentHashMap.put(), which replaces on
     * duplicate key — only the latest record for that ID should survive.
     */
    @Test
    void create_withDuplicateId_overwritesExistingRecord() {
        User original    = new User(1, "alice",  "alice@example.com",  "hash1");
        User replacement = new User(1, "alice2", "alice2@example.com", "hash2");

        store.create(original);
        store.create(replacement);

        assertEquals("alice2", store.get(1).getlogin());
    }
    /**
     * find() with a predicate that matches one record returns a
     * single-element list containing that record.
     */
    @Test
    void find_withMatchingPredicate_returnsSingleResult() {
        store.create(new User(1, "alice", "alice@example.com", "hashA"));
        store.create(new User(2, "bob",   "bob@example.com",   "hashB"));

        List<User> results = store.find(u -> u.getlogin().equals("alice"));

        assertEquals(1, results.size());
        assertEquals("alice", results.get(0).getlogin());
    }

    /**
     * find() with a predicate that matches multiple records returns all
     * of them — not just the first hit.
     */
    @Test
    void find_withPredicateMatchingMultiple_returnsAllMatches() {
        // Mark two users inactive, leave one active
        User u1 = new User(1, "alice", "alice@example.com", "h1");
        User u2 = new User(2, "bob",   "bob@example.com",   "h2");
        User u3 = new User(3, "carol", "carol@example.com", "h3");
        u1.setIsActive(false);
        u2.setIsActive(false);

        store.create(u1);
        store.create(u2);
        store.create(u3);

        List<User> inactive = store.find(u -> !u.getIsActive());

        assertEquals(2, inactive.size());
    }

    /**
     * find() with a predicate that matches nothing returns an empty
     * list, not null.
     */
    @Test
    void find_withNoMatchingPredicate_returnsEmptyList() {
        store.create(new User(1, "alice", "alice@example.com", "hashA"));

        List<User> results = store.find(u -> u.getlogin().equals("nobody"));

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
