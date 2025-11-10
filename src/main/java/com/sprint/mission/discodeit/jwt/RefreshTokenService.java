package com.sprint.mission.discodeit.jwt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final Map<String, String> refreshTokens = new ConcurrentHashMap<>();

  public void rotateRefreshToken(String username, String newToken){
    refreshTokens.put(username, newToken);
  }

  public boolean isValid(String username, String token){
    return token.equals(refreshTokens.get(username));
  }

}
