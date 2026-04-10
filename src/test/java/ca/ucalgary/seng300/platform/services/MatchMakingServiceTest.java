package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchMakingServiceTest {

    private final MatchMakingService service = new MatchMakingService();

    @AfterEach
    void clearQueues() {
        MatchMakingService.queues.clear();
    }

    @Test
    void addPlayerToQueueIncreasesQueueSize() {
        service.addPlayerToQueue(playerWithScore(101, GameType.TIC_TAC_TOE, 50.0));
        service.addPlayerToQueue(playerWithScore(102, GameType.TIC_TAC_TOE, 53.0));

        assertEquals(2, service.getQueueSize(GameType.TIC_TAC_TOE.toId()));
    }

    @Test
    void removePlayerRemovesOnlyMatchingPlayer() {
        PlayerStats p1 = playerWithScore(101, GameType.TIC_TAC_TOE, 50.0);
        PlayerStats p2 = playerWithScore(102, GameType.TIC_TAC_TOE, 53.0);
        service.addPlayerToQueue(p1);
        service.addPlayerToQueue(p2);

        MatchMakingService.removePlayer(p2);

        assertEquals(1, service.getQueueSize(GameType.TIC_TAC_TOE.toId()));
    }

    @Test
    void matchPlayersChoosesClosestPairAndRemovesThem() {
        PlayerStats p4 = playerWithScore(104, GameType.TIC_TAC_TOE, 35.0);
        PlayerStats p5 = playerWithScore(105, GameType.TIC_TAC_TOE, 35.1);
        PlayerStats p6 = playerWithScore(106, GameType.TIC_TAC_TOE, 34.0);
        service.addPlayerToQueue(p4);
        service.addPlayerToQueue(p5);
        service.addPlayerToQueue(p6);

        List<PlayerStats> matched = service.matchPlayers(GameType.TIC_TAC_TOE.toId());

        assertNotNull(matched);
        assertEquals(2, matched.size());
        assertEquals(104, matched.get(0).getUserID());
        assertEquals(105, matched.get(1).getUserID());
        assertEquals(1, service.getQueueSize(GameType.TIC_TAC_TOE.toId()));
    }

    @Test
    void matchPlayersReturnsNullWhenScoresTooFarApart() {
        service.addPlayerToQueue(playerWithScore(101, GameType.TIC_TAC_TOE, 50.0));
        service.addPlayerToQueue(playerWithScore(103, GameType.TIC_TAC_TOE, 90.0));

        List<PlayerStats> match = service.matchPlayers(GameType.TIC_TAC_TOE.toId());

        assertNull(match);
        assertEquals(2, service.getQueueSize(GameType.TIC_TAC_TOE.toId()));
    }

    @Test
    void queuesAreIndependentAcrossGameTypes() {
        PlayerStats ttt = playerWithScore(101, GameType.TIC_TAC_TOE, 50.0);
        PlayerStats c4a = playerWithScore(107, GameType.CONNECT4, 50.0);
        PlayerStats c4b = playerWithScore(108, GameType.CONNECT4, 55.0);
        service.addPlayerToQueue(ttt);
        service.addPlayerToQueue(c4a);
        service.addPlayerToQueue(c4b);

        List<PlayerStats> c4Match = service.matchPlayers(GameType.CONNECT4.toId());

        assertNotNull(c4Match);
        assertEquals(2, c4Match.size());
        assertEquals(1, service.getQueueSize(GameType.TIC_TAC_TOE.toId()));
        assertEquals(0, service.getQueueSize(GameType.CONNECT4.toId()));
    }

    private PlayerStats playerWithScore(int userId, GameType gameType, double score) {
        PlayerStats p = new PlayerStats(userId, gameType, userId);
        p.updateStats(score);
        return p;
    }
}
