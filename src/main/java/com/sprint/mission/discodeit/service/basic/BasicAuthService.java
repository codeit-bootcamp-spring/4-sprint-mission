package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;
  private final UserStatusService userStatusService;

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto patchRole(UserRoleUpdateRequest userRoleUpdateRequest){

    User user = userRepository.findById(userRoleUpdateRequest.userId())
        .orElseThrow(() -> UserNotFoundException.withId(userRoleUpdateRequest.userId()));

    user.setRole(userRoleUpdateRequest.newRole());
    user = userRepository.save(user);

    expireSessionsByUserId(user.getId());

    return userMapper.toDto(user, userStatusService.isOnline(user.getId()));
  }

  private void expireSessionsByUserId(UUID userId) {
    for (Object principal : sessionRegistry.getAllPrincipals()) {
      if (!(principal instanceof DiscodeitUserDetails ud)) continue;
      if (!ud.getUserDto().id().equals(userId)) continue;

      // false = 활성 세션만
      var sessions = sessionRegistry.getAllSessions(principal, false);
      for (SessionInformation si : sessions) {
        log.info("[SessionKill] userId={} sessionId={} expired", userId, si.getSessionId());
        si.expireNow();
      }
    }
  }
}
