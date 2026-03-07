package ca.ucalgary.seng300.platform.constants;

public class Constants {

    //file paths
    public static final String USER_FILE_PATH = "user.txt";
    public static final String GAME_FILE_PATH = "gameHistory.txt";
    public static final String SESSION_FILE_PATH = "sessionHistory.txt";
    public static final String PARTICIPANTS_FILE_PATH = "participants.txt";
    public static final String PLAYER_STATS_FILE_PATH = "playerStats.txt";

    //password hashing algorithm
    public static final String HASH_ALGORITHM = "SHA-256";

    // regex for verifying if email is generally compliant in RFC 5322 and RFC 5321 (Note this does not cover all cases but will cover 90 to 95% of all emails compliant in RFC standards)
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    //Ranks thresholds (these are up to but not including these values)
    public static final int TOTAL_ROUNDS_NEEDED_FOR_BRONZE = 10;
    public static final double BRONZE_SCORE_MAX_THRESHOLD = 14.0;
    public static final double SILVER_SCORE_MAX_THRESHOLD = 29.0;
    public static final double GOLD_SCORE_MAX_THRESHOLD = 44.0;
    public static final double PLATINUM_SCORE_MAX_THRESHOLD = 59.0;
    public static final double DIAMOND_SCORE_MAX_THRESHOLD = 74.0;
    public static final double MASTER_SCORE_MAX_THRESHOLD = 89.0;

}
