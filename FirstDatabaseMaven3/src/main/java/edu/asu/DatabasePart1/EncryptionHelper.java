package edu.asu.DatabasePart1;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionHelper {
    private static final String ALGORITHM = "AES";
    private static final int KEY_LENGTH = 16; // AES-128 bit key requires 16 bytes

    // Method to adjust key to 16 bytes by padding or truncating
    private static String adjustKey(String key) {
        if (key.length() >= KEY_LENGTH) {
            return key.substring(0, KEY_LENGTH);
        } else {
            return String.format("%-16s", key).substring(0, KEY_LENGTH); // Pads with spaces if needed
        }
    }

    // Encrypt a string using a key
    public static String encrypt(String data, String key) throws Exception {
        key = adjustKey(key); // Ensure key is exactly 16 bytes
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // Decrypt a string using a key
    public static String decrypt(String data, String key) throws Exception {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data to decrypt cannot be null or empty");
        }
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        // Add your decryption logic here
        // Example: Using AES for decryption
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(decryptedBytes);
    }

}
