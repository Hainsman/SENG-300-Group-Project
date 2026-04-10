package ca.ucalgary.seng300;

import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.server.PlatformCore;
import ca.ucalgary.seng300.platform.server.platformChannel;
import ca.ucalgary.seng300.platform.store.UserStore;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlatformCoreProfileUpdateTest {

    @Test
    void updateProfilePersistsToFile() throws Exception {
        PlatformCore core = new PlatformCore(false);
        Path userFile = createTempStoreFile();
        core.userStore = new UserStore(userFile.toString());
        core.userStore.create(new User(1, "alice", "alice@example.com", "oldHash"));

        platformChannel channel = new platformChannel(null, core);
        channel.setAuthenticatedSession(1, UUID.randomUUID());

        sysMSG response = core.updateProfile(channel, "alice2", "alice2@example.com", "newHash");
        assertEquals("PASS", response.getFieldA());

        // Reload from file to ensure persistence
        UserStore reloadedStore = new UserStore(userFile.toString());
        User updated = reloadedStore.get(1);
        assertEquals("alice2", updated.getlogin());
        assertEquals("alice2@example.com", updated.getEmail());
        assertEquals("newHash", updated.getPasswordHash());
    }

    @Test
    void updateProfileRejectsDuplicateEmail() throws Exception {
        PlatformCore core = new PlatformCore(false);
        Path userFile = createTempStoreFile();
        core.userStore = new UserStore(userFile.toString());
        core.userStore.create(new User(1, "alice", "alice@example.com", "oldHash"));
        core.userStore.create(new User(2, "bob", "bob@example.com", "hash2"));

        platformChannel channel = new platformChannel(null, core);
        channel.setAuthenticatedSession(1, UUID.randomUUID());

        sysMSG response = core.updateProfile(channel, "alice", "bob@example.com", "");
        assertEquals("FAIL", response.getFieldA());
        assertEquals("Email is already used by another account", response.getFieldB());
    }

    private Path createTempStoreFile() throws IOException {
        Path file = Files.createTempFile("user-store", ".json");
        Files.writeString(file, "{}");
        file.toFile().deleteOnExit();
        return file;
    }
}
