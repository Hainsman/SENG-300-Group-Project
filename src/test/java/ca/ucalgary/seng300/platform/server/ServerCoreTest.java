package ca.ucalgary.seng300.platform.server;

import ca.ucalgary.seng300.platform.messages.sysMSG;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ServerCoreTest {

    @Test
    void createGameRejectsBlankLobbyCode() throws Exception {
        PlatformCore core = new PlatformCore(false);
        sysMSG result = core.onCreateGame(" ", null);
        assertEquals("CREATE_GAME", result.getType());
        assertEquals("FAIL", result.getFieldA());
    }

    @Test
    void hostLobbyRejectsUnauthenticatedHost() throws Exception {
        PlatformCore core = new PlatformCore(false);
        platformChannel unauthenticatedHost = new platformChannel(null, core);
        boolean hosted = core.onHostLobby("ABCD", 1, unauthenticatedHost, "");
        assertEquals(false, hosted);
    }

    @Test
    void platformCoreCanBeBuiltWithoutStartingSocketLoop() throws Exception {
        PlatformCore core = new PlatformCore(false);
        assertNotNull(core.lobbies);
    }
}
