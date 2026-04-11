package ca.ucalgary.seng300;

import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.domain.Participant;
import ca.ucalgary.seng300.platform.server.PlatformCore;
import ca.ucalgary.seng300.platform.server.platformChannel;
import ca.ucalgary.seng300.platform.store.GameHistoryStore;
import ca.ucalgary.seng300.platform.store.ParticipantStore;
import ca.ucalgary.seng300.platform.store.PlayerStatsStore;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GameHistoryPersistenceTest {

    @Test
    void onAiGameResultPersistsHistoryAndParticipant() throws Exception {
        PlatformCore core = new PlatformCore(false);
        
        Path ghFile = createTempStoreFile("gameHistory");
        Path pFile = createTempStoreFile("participants");
        Path statsFile = createTempStoreFile("playerStats");
        
        core.gameHistoryStore = new GameHistoryStore(ghFile.toString());
        core.participantStore = new ParticipantStore(pFile.toString());
        core.playerStatsStore = new PlayerStatsStore(statsFile.toString());

        platformChannel channel = new platformChannel(null, core);
        channel.setAuthenticatedSession(10, UUID.randomUUID());

        // Simulate AI game WIN for TTT (gameId 1)
        core.onAiGameResult(channel, "1", "WIN");

        // Verify history
        List<GameHistory> histories = core.gameHistoryStore.findByGameID(1);
        assertEquals(1, histories.size());
        int ghId = histories.get(0).getID();

        // Verify participant
        List<Participant> participants = core.participantStore.findUserParticipationInGameEvents(10);
        assertEquals(1, participants.size());
        Participant p = participants.get(0);
        assertEquals(ghId, p.getGameHistoryID());
        assertEquals(1, p.getScore());
    }

    private Path createTempStoreFile(String prefix) throws IOException {
        Path file = Files.createTempFile(prefix, ".json");
        Files.writeString(file, "{}");
        file.toFile().deleteOnExit();
        return file;
    }
}
