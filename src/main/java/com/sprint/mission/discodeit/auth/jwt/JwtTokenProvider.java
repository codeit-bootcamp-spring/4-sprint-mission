package com.sprint.mission.discodeit.auth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtTokenProvider {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.access-token-expiration-minutes}")
  private int accessTokenExpirationMinutes;

  @Value("${jwt.refresh-token-expiration-minutes}")
  private int refreshTokenExpirationMinutes;

  /**
   * Access token 발급
   */
  public String generateAccessToken(Map<String, Object> claims, String subject) {
    try {
      JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));
      Date expiration = new Date(
          System.currentTimeMillis() +
              (long) accessTokenExpirationMinutes * 60 * 1000);

      JWTClaimsSet claimsSet = new Builder()
          .subject(subject)
          .claim("email", claims.get("email"))
          .claim("username", claims.get("username"))
          .claim("role", claims.get("role"))
          .expirationTime(expiration)
          .issueTime(new Date())
//          .issuer("discodeit.com")
          .build();

      SignedJWT signedJwt = new SignedJWT(
          new JWSHeader(JWSAlgorithm.HS256),
          claimsSet
      );

      signedJwt.sign(signer);
      return signedJwt.serialize();

    } catch (Exception e) {
      throw new RuntimeException("JWT 발급 실패", e);
    }
  }

  /**
   * Refresh token 발급
   */
  public String generateRefreshToken(String subject) {
    try {
      JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));
      Date expiration = new Date(
          System.currentTimeMillis() +
              (long) refreshTokenExpirationMinutes * 60 * 1000);

      JWTClaimsSet claimsSet = new Builder()
          .subject(subject)
          .expirationTime(expiration)
          .issueTime(new Date())
          .build();

      SignedJWT signedJwt = new SignedJWT(
          new JWSHeader(JWSAlgorithm.HS256),
          claimsSet
      );

      signedJwt.sign(signer);
      return signedJwt.serialize();

    } catch (Exception e) {
      throw new RuntimeException("JWT 발급 실패", e);
    }
  }

  public Map<String, Object> getClaims(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

      // signature 검증
      if (!signedJWT.verify(verifier)) {
        throw new RuntimeException("JWT 검증 실패");
      }
      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

      // expiration 검증
      if (new Date().after(claimsSet.getExpirationTime())) {
        throw new RuntimeException("JWT expired.");
      }

      return claimsSet.getClaims();

    } catch (Exception e) {
      throw new RuntimeException("JWT 파싱 실패", e);
    }
  }
}
