package com.sprint.mission.discodeit.service;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatusService {
  private final SessionRegistry sessionRegistry;

  public boolean isOnline(UUID userId) {
    for (Object principal : sessionRegistry.getAllPrincipals()) {
      if (principal instanceof DiscodeitUserDetails discodeitUserDetails &&
          discodeitUserDetails.getUserDto().id().equals(userId)) {
        return !sessionRegistry.getAllSessions(principal, false).isEmpty();
      }
    }
    return false;
  }
}
