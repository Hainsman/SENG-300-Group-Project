package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.store.PlayerStatsStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for LeaderBoardService.
 *
 * LeaderBoardService.buildLeaderBoard() calls PlayerStatsStore.getInstance()
 * internally — a Bill Pugh singleton backed by the real data file. There is
 * no seam to inject a test double, so these tests create records directly
 * in the live store and delete them in @AfterEach.
 *
 * To keep tests isolated from each other and from real data:
 *   - gameIds start at 9001 (incremented per test via nextGameId)
 *   - record IDs start at 900001 (incremented per record via nextRecordId)
 *   - each test uses a fresh gameId so the LeaderBoardService cache
 *     (keyed by gameId) is always cold — no stale entry from a prior run
 *
 * Because the cache entry for a fresh gameId is null, passing forceRecalc=false
 * still triggers a full build (the outer check: ts == null → enter write lock →
 * currentTs == null → buildLeaderBoard). No sleep or timing tricks are needed.
 *
 * Sorting priority defined in LeaderBoardService.buildLeaderBoard():
 *   1. Rank value descending  (GRAND_MASTER > MASTER > … > BRONZE)
 *   2. Score descending       (within same rank)
 *   3. Games played ascending (fewer games ranked higher, within same rank + score)
 *
 * Rank score thresholds (requires >= 10 rounds to leave BRONZE):
 *   BRONZE        0.0 – 14.0
 *   SILVER       14.0 – 29.0
 *   GOLD         29.0 – 44.0
 *   PLATINUM     44.0 – 59.0
 *   DIAMOND      59.0 – 74.0
 *   MASTER       74.0 – 89.0
 *   GRAND_MASTER 89.0 – MAX_VALUE
 */
class LeaderBoardServiceTest {

    // Static counters ensure unique IDs even though JUnit 5 creates
    // a new test class instance per test method.
    private static int nextGameId    = 9001;
    private static int nextRecordId  = 900001;

    private final LeaderBoardService service = LeaderBoardService.getInstance();
    private final PlayerStatsStore   store   = PlayerStatsStore.getInstance();

    /** Records created during a test — removed in tearDown(). */
    private final List<PlayerStats> created = new ArrayList<>();

    /**
     * Builds a PlayerStats with exactly 10 rounds and the given total score.
     * One scored call sets the score; nine zero-score calls bring the counter
     * to 10 so getRank() uses the score bracket rather than defaulting to BRONZE.
     * The record is persisted and registered for cleanup.
     */
    private PlayerStats makeStats(int gameId, int userId, double totalScore) {
        PlayerStats ps = new PlayerStats(nextRecordId++, gameId, userId);
        ps.updateStats(totalScore); // counter = 1, score = totalScore
        for (int i = 0; i < 9; i++) ps.updateStats(0.0); // counter reaches 10
        store.create(ps);
        created.add(ps);
        return ps;
    }

    /** Cleans up every record written by this test. */
    @AfterEach
    void tearDown() {
        for (PlayerStats ps : created) store.delete(ps);
        created.clear();
    }

    // ---------------------------------------------------------------
    // Sorting by rank
    // ---------------------------------------------------------------

    /**
     * A higher-ranked player must appear before a lower-ranked one.
     * GRAND_MASTER (90.0) > GOLD (30.0) > SILVER (14.0).
     */
    @Test
    void getLeaderBoard_sortsByRankDescending() {
        int gameId = nextGameId++;
        PlayerStats silver      = makeStats(gameId, 1, 14.0);
        PlayerStats gold        = makeStats(gameId, 2, 30.0);
        PlayerStats grandMaster = makeStats(gameId, 3, 90.0);

        List<PlayerStats> board = service.getLeaderBoard(false, gameId, 10);

        assertEquals(grandMaster.getUserID(), board.get(0).getUserID(),
                "GRAND_MASTER should be first");
        assertEquals(silver.getUserID(), board.get(board.size() - 1).getUserID(),
                "SILVER should be last");
    }

    /**
     * Within the same rank, the player with the higher score ranks first.
     * Both players are GOLD (29.0 – 44.0); the one with 43.0 wins.
     */
    @Test
    void getLeaderBoard_sameRank_sortsByScoreDescending() {
        int gameId = nextGameId++;
        PlayerStats lowGold  = makeStats(gameId, 1, 29.0);
        PlayerStats highGold = makeStats(gameId, 2, 43.0);

        List<PlayerStats> board = service.getLeaderBoard(false, gameId, 10);

        assertEquals(highGold.getUserID(), board.get(0).getUserID(),
                "Higher score within the same rank should rank first");
    }

    // ---------------------------------------------------------------
    // Count limiting (getSublistSafe)
    // ---------------------------------------------------------------

    /**
     * When count is less than the total number of records, exactly
     * count entries must be returned.
     */
    @Test
    void getLeaderBoard_countLimitsResults() {
        int gameId = nextGameId++;
        for (int i = 0; i < 5; i++) makeStats(gameId, i + 1, i * 5.0);

        List<PlayerStats> board = service.getLeaderBoard(false, gameId, 3);

        assertEquals(3, board.size());
    }

    /**
     * When count exceeds the number of available records, all records
     * are returned without throwing.
     */
    @Test
    void getLeaderBoard_countLargerThanResults_returnsAllAvailable() {
        int gameId = nextGameId++;
        makeStats(gameId, 1, 5.0);
        makeStats(gameId, 2, 10.0);

        List<PlayerStats> board = service.getLeaderBoard(false, gameId, 100);

        assertEquals(2, board.size());
    }

    // ---------------------------------------------------------------
    // Edge cases
    // ---------------------------------------------------------------

    /**
     * A gameId with no records at all must return an empty list —
     * not null and not an exception.
     */
    @Test
    void getLeaderBoard_noRecordsForGameId_returnsEmptyList() {
        int gameId = nextGameId++; // no makeStats() call — intentionally empty

        List<PlayerStats> board = service.getLeaderBoard(false, gameId, 10);

        assertNotNull(board);
        assertTrue(board.isEmpty());
    }
}
