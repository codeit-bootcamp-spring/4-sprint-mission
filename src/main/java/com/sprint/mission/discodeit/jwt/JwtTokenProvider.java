package com.sprint.mission.discodeit.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

  @Getter
  @Value("${jwt.key}")
  private String secretKey;

  @Getter
  @Value("${jwt.access-token-expiration-minutes}")
  private int accessTokenExpirationMinutes;

  @Getter
  @Value("${jwt.refresh-token-expiration-minutes}")
  private int refreshTokenExpirationMinutes;

//  // Base64 인코딩
//  public String encodedBase64SecretKey(String secretKey) {
//    return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
//  }

  //Access Token 생성
  public String generateAccessToken(Map<String, Object> claims,
      String subject,
      Date expiration) throws JOSEException {

    JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(subject)
        .expirationTime(expiration)
        .issueTime(new Date())
        .claim("roles", claims.get("roles"))
        .build();

    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    signedJWT.sign(signer);
    return signedJWT.serialize();
  }

  //RefreshToken 생성
  public String generateRefreshToken(String subject, Date expiration)
      throws JOSEException {


    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(subject)
        .expirationTime(expiration)
        .issueTime(new Date())
        .build();

    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    signedJWT.sign(new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8)));
    return signedJWT.serialize();
  }

  //Claims 추출
  public JWTClaimsSet getClaims(String jws)
      throws ParseException, JOSEException {

     SignedJWT signedJWT = SignedJWT.parse(jws);
     signedJWT.verify(new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8)));

     return signedJWT.getJWTClaimsSet();

  }

  //시그니처 검증
  public void verifySignature(String jws)
      throws ParseException, JOSEException {

     SignedJWT signedJWT = SignedJWT.parse(jws);
     if (!signedJWT.verify(new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8)))) throw new SecurityException("서명 검증 실패");

  }

  //만료 시간 계산
  public Date getTokenExpiration(int expirationMinutes) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MINUTE, expirationMinutes);
    Date expiration = calendar.getTime();

    return expiration;
  }

  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      boolean isVerified =  signedJWT.verify(new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8)));
      Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
      return isVerified && expiration.after(new Date());
    } catch (Exception e) {
      log.error("Invalid JWT token", e);
      return false;
    }
  }

  public String getEmailFromToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      return null;
    }
  }

  public String getUsername(String token) {
    return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public long getRefreshTokenValidity() {
    return refreshTokenExpirationMinutes * 60L;
  }

  // subject(email or username) 추출
  public String getSubject(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      return null;
    }
  }
}
