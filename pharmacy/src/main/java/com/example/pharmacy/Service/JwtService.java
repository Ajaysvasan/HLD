package com.example.pharmacy.Service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {
  private static final String ALGORITHM = "HmacSHA256";
  private static final long EXPIRY_SECONDS = 3600;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final byte[] secretKey;

  public JwtService() {
    this.secretKey = new byte[32];
    new SecureRandom().nextBytes(secretKey);
  }

  public String generateToken(String email) {
    try {
      Instant now = Instant.now();

      Map<String, Object> header = new LinkedHashMap<>();
      header.put("alg", "HS256");
      header.put("typ", "JWT");

      Map<String, Object> payload = new LinkedHashMap<>();
      payload.put("sub", email);
      payload.put("iat", now.getEpochSecond());
      payload.put("exp", now.plusSeconds(EXPIRY_SECONDS).getEpochSecond());

      String headerB64 = encode(objectMapper.writeValueAsBytes(header));
      String payloadB64 = encode(objectMapper.writeValueAsBytes(payload));
      String signingInput = headerB64 + "." + payloadB64;
      String signatureB64 = encode(sign(signingInput));

      return signingInput + "." + signatureB64;
    } catch (Exception e) {
      throw new IllegalStateException("Unable to generate JWT", e);
    }
  }

  public Optional<String> validateAndGetSubject(String token) {
    try {
      String[] parts = token.split("\\.");
      if (parts.length != 3) {
        return Optional.empty();
      }

      String signingInput = parts[0] + "." + parts[1];
      byte[] expectedSignature = sign(signingInput);
      byte[] actualSignature = Base64.getUrlDecoder().decode(parts[2]);
      if (!MessageDigest.isEqual(expectedSignature, actualSignature)) {
        return Optional.empty();
      }

      Map<String, Object> payload =
          objectMapper.readValue(
              Base64.getUrlDecoder().decode(parts[1]), new TypeReference<Map<String, Object>>() {});

      long expiresAt = ((Number) payload.get("exp")).longValue();
      if (Instant.now().getEpochSecond() > expiresAt) {
        return Optional.empty();
      }

      return Optional.of((String) payload.get("sub"));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private byte[] sign(String data) throws Exception {
    Mac mac = Mac.getInstance(ALGORITHM);
    mac.init(new SecretKeySpec(secretKey, ALGORITHM));
    return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
  }

  private String encode(byte[] bytes) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}
