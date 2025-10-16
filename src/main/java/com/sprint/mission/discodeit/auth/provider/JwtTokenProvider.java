package com.sprint.mission.discodeit.auth.provider;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ExpiredJWTException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
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

    public String generateAccessToken(Map<String, Object> claims, String subject) {
        log.info("AccessToken 발급 시도 subject = {}", subject);
        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

            Date expiration = new Date(System.currentTimeMillis() + accessTokenExpirationMinutes * 1000 * 60);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .claim("roles", claims.get("roles"))
                    .claim("username", claims.get("username"))
                    .expirationTime(expiration)
                    .issueTime(new Date())
                    .issuer("discodeit")
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );
            signedJWT.sign(signer);
            log.info("AccessToken 발급 성공 subject = {}", subject);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWT 발급 실패", e);
        }
    }

    public String generateRefreshToken(String subject) {
        log.info("RefreshToken 발급 시도 subject = {}", subject);
        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

            Date expiration = new Date(System.currentTimeMillis() + refreshTokenExpirationMinutes * 60 * 1000);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .expirationTime(expiration)
                    .issueTime(new Date())
                    .issuer("discodeit")
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );
            signedJWT.sign(signer);
            log.info("RefreshToken 발급 성공 subject = {}", subject);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWT 발급 실패", e);
        }
    }

    public Map<String, Object> getClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("JWT 검증 실패");
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            return claimsSet.getClaims();
        } catch (Exception e) {
            throw new RuntimeException("JWT 파싱 실패", e);
        }
    }

    public void verifyJws(String jws) {
        log.info("JWS 검증 시도");
        try {
            SignedJWT jwt = SignedJWT.parse(jws);
            jwt.verify(new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8)));
            if (jwt.getJWTClaimsSet().getExpirationTime().before(new Date())) {
                throw new ExpiredJWTException("Access Token Expired");
            }
            log.info("JWS 검증 성공");
        } catch (Exception e) {
            throw new RuntimeException("JWT 검증 실패", e);
        }
    }

    public String getUsername(String token) throws ParseException {
        SignedJWT jwt = SignedJWT.parse(token);
        String username = jwt.getJWTClaimsSet()
                .getClaim("username").toString();
        return username;
    }

    public boolean isExpired(String token) {
        log.info("Token 유효기간 검증 시도");
        try {
            Date now = new Date();
            SignedJWT jwt = SignedJWT.parse(token);
            if (jwt.getJWTClaimsSet().getExpirationTime().before(now)) {
                log.info("Token의 유효기간 만료 subject = {}", jwt.getJWTClaimsSet().getSubject());
                return true;
            }
            log.info("Token의 유효기간 검증 성공 subject = {}", jwt.getJWTClaimsSet().getSubject());
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Token의 유효기간 검증 중 오류 발생", e);
        }
    }
}
