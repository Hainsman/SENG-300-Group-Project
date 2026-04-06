package ca.ucalgary.seng300.platform.constants;

public class Constants {

    /** Object-stream game server ({@link ca.ucalgary.seng300.PlatformCore}) — same port as {@link ca.ucalgary.seng300.platform.client.ClientCore}. */
    public static final int PLATFORM_GAME_PORT = 2020;

    //file paths
    public static final String USER_FILE_PATH = "datafiles/user.txt";
    public static final String GAME_FILE_PATH = "datafiles/gameHistory.txt";
    public static final String SESSION_FILE_PATH = "datafiles/sessionHistory.txt";
    public static final String PARTICIPANTS_FILE_PATH = "datafiles/participants.txt";
    public static final String PLAYER_STATS_FILE_PATH = "datafiles/playerStats.txt";
    public static final String FRIENDS_FILE_PATH = "datafiles/friends.txt";

    //password hashing algorithm
    public static final String HASH_ALGORITHM = "SHA-256";

    // regex for verifying if email is generally compliant in RFC 5322 and RFC 5321 (Note this does not cover all cases but will cover 90 to 95% of all emails compliant in RFC standards)
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    //Ranks thresholds (these are up to but not including these values)
    public static final int TOTAL_ROUNDS_NEEDED_FOR_BRONZE = 10;
}
