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

}
