package ca.ucalgary.seng300.platform.stubs;

import ca.ucalgary.seng300.model.Player;
import ca.ucalgary.seng300.model.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsDatabaseTest {

    private CredentialsDatabase db;

    @BeforeEach
    void setUp() {
        db = new CredentialsDatabase();
    }

    // --- addNewPlayer ---

    @Test
    void addNewPlayer_withValidData_returnsTrue() {
        boolean result = db.addNewPlayer("alice", "alice@example.com", "pass123", Rank.BRONZE);
        assertTrue(result);
    }

    @Test
    void addNewPlayer_withValidData_playerIsStored() {
        db.addNewPlayer("alice", "alice@example.com", "pass123", Rank.BRONZE);
        assertNotNull(db.findPlayerByUsername("alice"));
    }

    @Test
    void addNewPlayer_withDuplicateUsername_returnsFalse() {
        db.addNewPlayer("alice", "alice@example.com", "pass123", Rank.BRONZE);
        boolean result = db.addNewPlayer("alice", "other@example.com", "pass456", Rank.SILVER);
        assertFalse(result);
    }

    @Test
    void addNewPlayer_withDuplicateUsername_doesNotOverwriteExistingPlayer() {
        db.addNewPlayer("alice", "alice@example.com", "pass123", Rank.BRONZE);
        db.addNewPlayer("alice", "other@example.com", "pass456", Rank.SILVER);
        assertEquals("alice@example.com", db.findPlayerByUsername("alice").getEmail());
    }

    @Test
    void addNewPlayer_withNullUsername_returnsFalse() {
        boolean result = db.addNewPlayer(null, "alice@example.com", "pass123", Rank.BRONZE);
        assertFalse(result);
    }

    // --- usernameLookup ---

    @Test
    void usernameLookup_forExistingUsername_returnsTrue() {
        db.addNewPlayer("bob", "bob@example.com", "pass", Rank.GOLD);
        assertTrue(db.usernameLookup("bob"));
    }

    @Test
    void usernameLookup_forNonExistentUsername_returnsFalse() {
        assertFalse(db.usernameLookup("nobody"));
    }

    // --- findPlayerByUsername ---

    @Test
    void findPlayerByUsername_forExistingPlayer_returnsCorrectPlayer() {
        db.addNewPlayer("carol", "carol@example.com", "secret", Rank.PLATINUM);
        Player player = db.findPlayerByUsername("carol");
        assertEquals("carol", player.getUsername());
        assertEquals("carol@example.com", player.getEmail());
        assertEquals(Rank.PLATINUM, player.getRanking());
    }

    @Test
    void findPlayerByUsername_forNonExistentPlayer_returnsNull() {
        assertNull(db.findPlayerByUsername("ghost"));
    }

    // --- deleteExistingPlayer ---

    @Test
    void deleteExistingPlayer_forExistingPlayer_returnsTrue() {
        db.addNewPlayer("dave", "dave@example.com", "pass", Rank.BRONZE);
        boolean result = db.deleteExistingPlayer("dave");
        assertTrue(result);
    }

    @Test
    void deleteExistingPlayer_forExistingPlayer_playerIsRemoved() {
        db.addNewPlayer("dave", "dave@example.com", "pass", Rank.BRONZE);
        db.deleteExistingPlayer("dave");
        assertNull(db.findPlayerByUsername("dave"));
    }

    @Test
    void deleteExistingPlayer_forExistingPlayer_usernameLookupReturnsFalse() {
        db.addNewPlayer("dave", "dave@example.com", "pass", Rank.BRONZE);
        db.deleteExistingPlayer("dave");
        assertFalse(db.usernameLookup("dave"));
    }

    @Test
    void deleteExistingPlayer_forNonExistentPlayer_returnsFalse() {
        boolean result = db.deleteExistingPlayer("nobody");
        assertFalse(result);
    }

    // --- saveDatabase / loadDatabase ---

    @Test
    void saveDatabase_andLoadDatabase_restoresAllPlayers(@TempDir Path tempDir) {
        db.addNewPlayer("eve", "eve@example.com", "pass", Rank.DIAMOND);
        db.addNewPlayer("frank", "frank@example.com", "pass2", Rank.MASTER);

        String path = tempDir.resolve("creds.json").toString();
        db.saveDatabase(path);

        CredentialsDatabase loaded = new CredentialsDatabase();
        loaded.loadDatabase(path);

        assertNotNull(loaded.findPlayerByUsername("eve"));
        assertNotNull(loaded.findPlayerByUsername("frank"));
    }

    @Test
    void saveDatabase_andLoadDatabase_preservesPlayerDetails(@TempDir Path tempDir) {
        db.addNewPlayer("eve", "eve@example.com", "pass", Rank.DIAMOND);

        String path = tempDir.resolve("creds.json").toString();
        db.saveDatabase(path);

        CredentialsDatabase loaded = new CredentialsDatabase();
        loaded.loadDatabase(path);

        Player player = loaded.findPlayerByUsername("eve");
        assertEquals("eve", player.getUsername());
        assertEquals("eve@example.com", player.getEmail());
    }

    @Test
    void loadDatabase_withNonExistentFile_leavesDbEmpty() {
        db.loadDatabase("this_file_does_not_exist.json");
        assertFalse(db.usernameLookup("anyone"));
    }
}
