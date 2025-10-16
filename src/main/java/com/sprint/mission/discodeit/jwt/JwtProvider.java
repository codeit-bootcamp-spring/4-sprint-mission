package com.sprint.mission.discodeit.jwt;


import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider  {

  private final JwtTokenizer jwtTokenizer;

  public String createAccessToken(DiscodeitUserDetails ud) {
    String base64 = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
    Date exp = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", ud.getUserDto().id().toString());
    claims.put("username", ud.getUserDto().username());
    claims.put("roles", ud.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

    return jwtTokenizer.generateAccessToken(
        claims,
        ud.getUsername(),  // subject
        exp,
        base64
    );
  }

  public String createRefreshToken(String subject) {
    String base64 = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
    Date exp = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
    return jwtTokenizer.generateRefreshToken(subject, exp, base64);
  }

  public long getAccessTtlSeconds() {
    return Duration.ofMinutes(jwtTokenizer.getAccessTokenExpirationMinutes()).toSeconds();
  }

  public long getRefreshTtlSeconds() {
    return Duration.ofMinutes(jwtTokenizer.getRefreshTokenExpirationMinutes()).toSeconds();
  }

  public boolean validateAccessToken(String accessToken) {
    try {
      String base64 = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
      jwtTokenizer.verifySignature(accessToken, base64); // 서명/만료 검증(만료 시 예외)
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String getSubject(String accessToken){
    String base64 = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
    return jwtTokenizer.getClaims(accessToken, base64).getBody().getSubject();
  }
}