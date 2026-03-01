package ca.ucalgary.seng300.platform.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    private static String bytesToHex(byte[] hash){
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash){
            hexString.append(String.format("%X", b));
        }
        return hexString.toString();
    }
}
