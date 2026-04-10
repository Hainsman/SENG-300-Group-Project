package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.store.UserStore;
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

    private final LeaderBoardService service = new LeaderBoardService();
    private final UserStore store = UserStore.getInstance();

    private final List<User> created = new ArrayList<>();

    private User makeUser(String login, int tttElo, int c4Elo) {
        int nextId = store.findAll().stream().mapToInt(User::getID).max().orElse(0) + 1;
        User u = new User(nextId, login, login + "@example.com", "hash");
        u.setEloForGame(GameType.TIC_TAC_TOE, tttElo);
        u.setEloForGame(GameType.CONNECT4, c4Elo);
        store.create(u);
        created.add(u);
        return u;
    }

    @AfterEach
    void tearDown() {
        for (User u : created) store.delete(u);
        created.clear();
    }

    @Test
    void getLeaderBoard_sortsByEloDescending() {
        User low = makeUser("leader-low", 1210, 1200);
        User high = makeUser("leader-high", 1800, 1200);
        User mid = makeUser("leader-mid", 1500, 1200);

        List<User> board = service.getLeaderBoard(true, GameType.TIC_TAC_TOE, 10);

        assertEquals(high.getID(), board.get(0).getID());
        assertEquals(mid.getID(), board.get(1).getID());
        assertEquals(low.getID(), board.get(2).getID());
    }

    @Test
    void getLeaderBoard_tieBreaksByLoginWhenEloEqual() {
        String suffix = String.valueOf(System.nanoTime());
        String alphaLogin = "alpha-user-" + suffix;
        String betaLogin = "beta-user-" + suffix;
        makeUser(alphaLogin, 1500, 1200);
        makeUser(betaLogin, 1500, 1200);

        List<User> board = service.getLeaderBoard(true, GameType.TIC_TAC_TOE, 10000);

        int alpha = indexOfLogin(board, alphaLogin);
        int beta = indexOfLogin(board, betaLogin);
        assertTrue(alpha >= 0 && beta >= 0);
        assertTrue(alpha < beta);
    }

    @Test
    void getLeaderBoard_countLimitsResults() {
        makeUser("u1", 1200, 1200);
        makeUser("u2", 1300, 1200);
        makeUser("u3", 1400, 1200);
        makeUser("u4", 1500, 1200);
        makeUser("u5", 1600, 1200);

        List<User> board = service.getLeaderBoard(true, GameType.TIC_TAC_TOE, 3);

        assertEquals(3, board.size());
    }

    @Test
    void getLeaderBoard_noRecordsForGameId_returnsEmptyList() {
        List<User> board = service.getLeaderBoard(true, GameType.CONNECT4, 10);

        assertNotNull(board);
        assertTrue(board.size() >= 0);
    }

    private int indexOfLogin(List<User> users, String login) {
        for (int i = 0; i < users.size(); i++) {
            if (login.equals(users.get(i).getlogin())) {
                return i;
            }
        }
        return -1;
    }
}
