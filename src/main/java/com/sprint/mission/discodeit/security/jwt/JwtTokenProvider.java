package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/*
 * 토큰의 발급, 갱신, 유효성 검사 담당
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserRepository userRepository;

    @Getter
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private long accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private long refreshTokenExpirationMinutes;

    public String generateAccessToken(Map<String, Object> claims) {
        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8)); // signature 생성

            Date expiration = new Date(System.currentTimeMillis() + accessTokenExpirationMinutes * 60 * 1000); // accessTokenExpirationMinutes: 30분

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder() // payload
                    .subject(claims.get("userId").toString())
                    .claim("roles", claims.get("roles"))
                    .expirationTime(expiration)
                    .issueTime(new Date())
                    .issuer("discodeit.com") // 토큰 발급 주체
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet); // header + payload
            signedJWT.sign(signer); // (header + payload) + signature

            log.info("# Generated Access Token: {}", signedJWT.serialize());
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWT(Access Token) 발급 실패", e);
        }
    }

    public String generateRefreshToken(String subject) {
        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

            Date expiration = new Date(System.currentTimeMillis() + refreshTokenExpirationMinutes * 60 * 1000);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .expirationTime(expiration)
                    .issueTime(new Date())
                    .issuer("discodeit.com")
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);

            log.info("# Generated Refresh Token: {}", signedJWT.serialize());
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWT(Refresh Token) 발급 실패", e);
        }
    }

    public String renewAccessToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token");
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(refreshToken);
            String subject = signedJWT.getJWTClaimsSet().getSubject();
            UUID userId = UUID.fromString(subject);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> UserNotFoundException.withId(userId));

            Map<String, Object> claims = Map.of(
                    "userId", userId,
                    "roles", user.getRole().name()
            );

            log.info("# Renew Access Token, userId = {}, refresh token = {}", userId, refreshToken);

            return generateAccessToken(claims);
        } catch (Exception e) {
            throw new RuntimeException("# Access Token 재발급 실패", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

            return signedJWT.verify(verifier)
                    && signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date());
        } catch (Exception e) {
            log.warn("# Invalid JWT Token: {}, Message: {}", token, e.getMessage());
            return false;
        }
    }

    public Map<String, Object> getClaims(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getClaims();
        } catch (Exception e) {
            throw new RuntimeException("JWT Claims 추출 실패", e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
            return exp != null && exp.before(new Date());
        } catch (Exception e) {
            return false; // 파싱 자체가 안되면 false로 두고 validateToken()에서 걸리게 함
        }
    }
}
