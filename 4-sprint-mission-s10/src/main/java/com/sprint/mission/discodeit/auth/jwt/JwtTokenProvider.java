package com.sprint.mission.discodeit.auth.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Value("${jwt.refresh-token-expiration-days:7}")
    private int refreshTokenExpirationDays;

    public String generateAccessToken(Map<String, Object> claims, String subject) {
        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

            Date expiration = new Date(System.currentTimeMillis() + accessTokenExpirationMinutes * 60 * 1000);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .claim("roles", claims.get("roles"))
                    .expirationTime(expiration)
                    .issueTime(new Date())
                    .issuer("example.com")
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );

            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWT 발급 실패", e);
        }
    }

    public String generateRefreshToken(String subject) {
        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

            Date expiration = new Date(System.currentTimeMillis() + refreshTokenExpirationDays * 24L * 60 * 60 * 1000);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .expirationTime(expiration)
                    .issueTime(new Date())
                    .issuer("example.com")
                    .claim("token_type", "refresh")
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("리프레시 토큰 발급 실패", e);
        }
    }

    public Map<String, String> refresh(String refreshToken, Map<String, Object> newAccessClaims, boolean rotateRefresh) {
        try {
            SignedJWT jwt = SignedJWT.parse(refreshToken);

            JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));
            if (!jwt.verify(verifier)) {
                throw new RuntimeException("리프레시 토큰 서명 검증 실패");
            }

            JWTClaimsSet c = jwt.getJWTClaimsSet();
            if (c.getExpirationTime() == null || new Date().after(c.getExpirationTime())) {
                throw new RuntimeException("리프레시 토큰 만료");
            }
            if (!"example.com".equals(c.getIssuer())) {
                throw new RuntimeException("리프레시 토큰 issuer 불일치");
            }
            Object type = c.getClaim("token_type");
            if (!(type instanceof String) || !"refresh".equals(type)) {
                throw new RuntimeException("리프레시 토큰이 아닙니다");
            }

            String subject = c.getSubject();
            String newAccess = generateAccessToken(newAccessClaims, subject);

            String nextRefresh = rotateRefresh ? generateRefreshToken(subject) : refreshToken;

            Map<String, String> result = new HashMap<>();
            result.put("accessToken", newAccess);
            result.put("refreshToken", nextRefresh);
            return result;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("리프레시 처리 중 오류", e);
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
}
