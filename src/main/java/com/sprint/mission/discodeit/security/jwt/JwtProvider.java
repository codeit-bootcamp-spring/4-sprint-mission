package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    @Getter
    private final int accessTokenExpiration;

    @Getter
    private final int refreshTokenExpiration;

    private final JWSSigner signer;
    private final JWSVerifier verifier;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") int accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") int refreshTokenExpiration
    ) throws JOSEException {
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;

        byte[] secretBytes = Base64.getDecoder().decode(secret);
        this.signer = new MACSigner(secretBytes);
        this.verifier = new MACVerifier(secretBytes);
    }

    public String createAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
        return createToken(userDetails, accessTokenExpiration, signer);
    }

    public String createRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
        return createToken(userDetails, refreshTokenExpiration, signer);
    }

    private String createToken(DiscodeitUserDetails userDetails, int expiration, JWSSigner signer)
            throws JOSEException {
        String tokenId = UUID.randomUUID().toString();
        UserResponse user = userDetails.getUserResponse();

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration * 1000L);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(now)
                .jwtID(tokenId)
                .claim("id", user.getId())
                .expirationTime(expirationDate)
                .build();
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(verifier)) {
                return false;
            }

            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expiration != null && expiration.after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException("JWT 토큰 파싱 실패", e);
        }
    }


    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(refreshTokenExpiration / 1000);
        return refreshCookie;
    }

    public Cookie createRefreshTokenExpirationCookie() {
        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // 운영 HTTPS 사용
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        return refreshCookie;
    }

    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
