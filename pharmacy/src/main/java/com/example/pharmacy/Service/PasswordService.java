package com.example.pharmacy.Service;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

@Service
public class PasswordService {
  private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
  private static final int SALT_LENGTH_BYTES = 16;
  private static final int HASH_LENGTH_BITS = 256;
  private static final int ITERATIONS = 210_000;

  private final SecureRandom secureRandom = new SecureRandom();

  public String hash(String rawPassword) {
    byte[] salt = new byte[SALT_LENGTH_BYTES];
    secureRandom.nextBytes(salt);
    byte[] hash = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS);
    return ITERATIONS + ":" + encode(salt) + ":" + encode(hash);
  }

  public boolean matches(String rawPassword, String stored) {
    String[] parts = stored.split(":");
    if (parts.length != 3) {
      return false;
    }
    int iterations = Integer.parseInt(parts[0]);
    byte[] salt = decode(parts[1]);
    byte[] expectedHash = decode(parts[2]);
    byte[] actualHash = pbkdf2(rawPassword.toCharArray(), salt, iterations);
    return MessageDigest.isEqual(expectedHash, actualHash);
  }

  private byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
    try {
      PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, HASH_LENGTH_BITS);
      SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
      return factory.generateSecret(spec).getEncoded();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IllegalStateException("Unable to hash password", e);
    } finally {
      Arrays.fill(password, '\0');
    }
  }

  private String encode(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }

  private byte[] decode(String value) {
    return Base64.getDecoder().decode(value);
  }
}
