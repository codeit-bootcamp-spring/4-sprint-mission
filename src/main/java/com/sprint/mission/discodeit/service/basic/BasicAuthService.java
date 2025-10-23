package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public UserDto updateRole(RoleUpdateRequest roleUpdateRequest) {
    UUID userId = roleUpdateRequest.userId();
    Role role = roleUpdateRequest.role();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    user.updateRole(role);

    // finds the user's principal in the SessionRegistry and expire their active sessions
    List<DiscodeitUserDetails> currentUserDetailsList = sessionRegistry.getAllPrincipals().stream()
        .filter(p -> p instanceof DiscodeitUserDetails)
        .map(p -> (DiscodeitUserDetails) p)
        .toList();

    currentUserDetailsList.stream()
        .filter(d -> d.getUserDto().userId().equals(user.getId()))
        .findFirst()
        .ifPresent(userDetails -> {
          List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
          sessions.forEach(SessionInformation::expireNow);
        });

    return userMapper.toDto(user);
  }

  @Transactional
  public boolean isUserOnline(String username) {
    if (username == null) {
      return false;
    }

    return sessionRegistry.getAllPrincipals().stream()
        .filter(p -> p instanceof DiscodeitUserDetails)
        .map(p -> (DiscodeitUserDetails) p)
        .anyMatch(userDetails -> userDetails.getUsername().equals(username));
  }


}
