package ca.ucalgary.seng300.platform.utils;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.constants.Rank;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Helper utility class.
 *
 * Helper has three static methods:
 *   - getRank(totalRoundsPlayed, score)  — maps game stats to a Rank enum
 *   - isEmailValid(email, regex)         — validates an email against a regex
 *   - hashPassword(password, algorithm)  — hashes a password string
 *
 * getRank() is the highest priority because LeaderBoardService uses it
 * to sort every leaderboard entry. All seven rank boundaries are tested.
 *
 * Rank score thresholds (from the Rank enum):
 *   BRONZE       0.0  – 14.0
 *   SILVER      14.0  – 29.0
 *   GOLD        29.0  – 44.0
 *   PLATINUM    44.0  – 59.0
 *   DIAMOND     59.0  – 74.0
 *   MASTER      74.0  – 89.0
 *   GRAND_MASTER 89.0 – MAX_VALUE
 *
 * Players need at least Constants.TOTAL_ROUNDS_NEEDED_FOR_BRONZE (10)
 * rounds before their score is considered — fewer rounds always gives BRONZE.
 */
class HelperTest {

    /**
     * A player with fewer than 10 rounds must always receive BRONZE,
     * even if their score would otherwise qualify for a higher rank.
     */
    @Test
    void getRank_fewerThanTenRounds_alwaysReturnsBronze() {
        assertEquals(Rank.BRONZE, Helper.getRank(0,  999.0));
        assertEquals(Rank.BRONZE, Helper.getRank(9,  999.0));
    }

    /**
     * At exactly 10 rounds, rank is determined by score.
     * A score of 0.0 falls in the BRONZE bracket (0.0 – 14.0).
     */
    @Test
    void getRank_tenRoundsLowScore_returnsBronze() {
        assertEquals(Rank.BRONZE, Helper.getRank(10, 0.0));
    }

    /**
     * Score of 14.0 is the lower boundary of SILVER (14.0 – 29.0).
     */
    @Test
    void getRank_scoreAtSilverMin_returnsSilver() {
        assertEquals(Rank.SILVER, Helper.getRank(10, 14.0));
    }

    /**
     * Score of 29.0 is the lower boundary of GOLD (29.0 – 44.0).
     */
    @Test
    void getRank_scoreAtGoldMin_returnsGold() {
        assertEquals(Rank.GOLD, Helper.getRank(10, 29.0));
    }

    /**
     * Score of 44.0 is the lower boundary of PLATINUM (44.0 – 59.0).
     */
    @Test
    void getRank_scoreAtPlatinumMin_returnsPlatinum() {
        assertEquals(Rank.PLATINUM, Helper.getRank(10, 44.0));
    }

    /**
     * Score of 59.0 is the lower boundary of DIAMOND (59.0 – 74.0).
     */
    @Test
    void getRank_scoreAtDiamondMin_returnsDiamond() {
        assertEquals(Rank.DIAMOND, Helper.getRank(10, 59.0));
    }

    /**
     * Score of 74.0 is the lower boundary of MASTER (74.0 – 89.0).
     */
    @Test
    void getRank_scoreAtMasterMin_returnsMaster() {
        assertEquals(Rank.MASTER, Helper.getRank(10, 74.0));
    }

    /**
     * Score of 89.0 reaches GRAND_MASTER (89.0 – MAX_VALUE).
     * The for-loop in getRank() falls through without a match and
     * returns GRAND_MASTER explicitly.
     */
    @Test
    void getRank_scoreAtGrandMasterMin_returnsGrandMaster() {
        assertEquals(Rank.GRAND_MASTER, Helper.getRank(10, 89.0));
    }

    /**
     * A score just below a boundary must stay in the lower rank.
     * 13.99 is inside BRONZE, not SILVER.
     */
    @Test
    void getRank_scoreBelowSilverBoundary_returnsBronze() {
        assertEquals(Rank.BRONZE, Helper.getRank(10, 13.99));
    }



    /**
     * A well-formed email address must return true.
     */
    @Test
    void isEmailValid_validEmail_returnsTrue() {
        assertTrue(Helper.isEmailValid("user@example.com", Constants.EMAIL_REGEX));
    }

    /**
     * A valid email with a subdomain must also pass.
     */
    @Test
    void isEmailValid_subdomainEmail_returnsTrue() {
        assertTrue(Helper.isEmailValid("user@mail.example.com", Constants.EMAIL_REGEX));
    }

    /**
     * null must return false without throwing a NullPointerException.
     */
    @Test
    void isEmailValid_nullInput_returnsFalse() {
        assertFalse(Helper.isEmailValid(null, Constants.EMAIL_REGEX));
    }

    /**
     * An empty string must return false.
     */
    @Test
    void isEmailValid_emptyString_returnsFalse() {
        assertFalse(Helper.isEmailValid("", Constants.EMAIL_REGEX));
    }

    /**
     * An address missing the @ symbol is not a valid email.
     */
    @Test
    void isEmailValid_missingAtSymbol_returnsFalse() {
        assertFalse(Helper.isEmailValid("userexample.com", Constants.EMAIL_REGEX));
    }

    /**
     * An address missing the domain part after @ is not valid.
     */
    @Test
    void isEmailValid_missingDomain_returnsFalse() {
        assertFalse(Helper.isEmailValid("user@", Constants.EMAIL_REGEX));
    }

    /**
     * Hashing the same password twice must produce the same result.
     * MessageDigest is deterministic — identical input → identical output.
     */
    @Test
    void hashPassword_sameInput_producesIdenticalHash() {
        String first  = Helper.hashPassword("password123", Constants.HASH_ALGORITHM);
        String second = Helper.hashPassword("password123", Constants.HASH_ALGORITHM);

        assertEquals(first, second);
    }

    /**
     * Two different passwords must not produce the same hash.
     */
    @Test
    void hashPassword_differentInputs_produceDifferentHashes() {
        String hashA = Helper.hashPassword("password123", Constants.HASH_ALGORITHM);
        String hashB = Helper.hashPassword("password456", Constants.HASH_ALGORITHM);

        assertNotEquals(hashA, hashB);
    }

    /**
     * The returned hash must be a non-empty string — the method
     * must not return null or an empty string on valid input.
     */
    @Test
    void hashPassword_validInput_returnsNonEmptyString() {
        String hash = Helper.hashPassword("anyPassword", Constants.HASH_ALGORITHM);

        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }
}
