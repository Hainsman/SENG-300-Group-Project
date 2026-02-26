package ca.ucalgary.seng300.platform.constants;

public class Constants {
    //file paths
    public static final String USERFILEPATH = "user.txt";
    public static final String GAMEFILEPATH = "gameHistory.txt";
    public static final String SESSIONFILEPATH = "sessionHistory.txt";
    public static final String PARTICIPANTSFILEPATH = "participants.txt";

    //password hashing algorithm
    public static final String HASHALGORITHM = "SHA-256";

    // regex for verifying if email is generally compliant in RFC 5322 and RFC 5321 (Note this does not cover all cases but will cover 90 to 95% of all emails compliant in RFC standards)
    public static final String EMAILREGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
}
