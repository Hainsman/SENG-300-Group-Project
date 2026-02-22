package ca.ucalgary.seng300.platform.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    private static String bytesToHex(byte[] hash){
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash){
            hexString.append(String.format("%X", b));
        }
        return hexString.toString();
    }
}
