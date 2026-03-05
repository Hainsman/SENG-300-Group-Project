package ca.ucalgary.seng300.platform.utils;
import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.domain.Participants;
import ca.ucalgary.seng300.platform.store.Store;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Helper {
    public static String hashPassword(String password, String algorithm){
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = digest.digest(password.getBytes());
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isEmailValid(String email, String emailRegex){
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null || email.isEmpty()){
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static Rank scoreToRank(Store<Participants> storeParticipants, Store<GameHistory> storeGameHistory, int gameID, int userID){
        int totalRoundsPlayed= 0;
        double score = 0;

        List<Participants> participantsList = storeParticipants.find(rec -> rec.getUserID() == userID); //returns list with corresponding userID

        for (Participants participant : participantsList) {
            GameHistory gh = storeGameHistory.get(participant.getGameHistoryID());
            if (gh.getGameID() == gameID){
                totalRoundsPlayed++;
                score += participant.getScore();
            }
        }
        return getRank(totalRoundsPlayed, score);
    }

    private static Rank getRank(int totalRoundsPlayed, double score){
        if (totalRoundsPlayed < Constants.TOTAL_ROUNDS_NEEDED_FOR_BRONZE || score < Constants.BRONZE_SCORE_MAX_THRESHOLD)
            return Rank.BRONZE;

        if (score < Constants.SILVER_SCORE_MAX_THRESHOLD)
            return Rank.SILVER;

        if (score < Constants.GOLD_SCORE_MAX_THRESHOLD)
            return Rank.GOLD;

        if (score < Constants.PLATINUM_SCORE_MAX_THRESHOLD)
            return Rank.PLATINUM;

        if (score < Constants.DIAMOND_SCORE_MAX_THRESHOLD)
            return Rank.DIAMOND;

        if (score < Constants.MASTER_SCORE_MAX_THRESHOLD)
            return Rank.MASTER;

        return Rank.GRAND_MASTER;
    }

    private static String bytesToHex(byte[] hash){
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash){
            hexString.append(String.format("%X", b));
        }
        return hexString.toString();
    }
}
