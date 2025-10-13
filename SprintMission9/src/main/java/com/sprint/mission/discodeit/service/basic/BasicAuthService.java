package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public UserDto updateUserRole(RoleUpdateRequest roleUpdateRequest) {
    UUID userId = roleUpdateRequest.userId();
    log.debug("사용자 권한수정 시작: id={}", userId);
    UserDto userDto =
        userRepository
            .findById(userId)
            .map(
                user -> {
                  user.updateRole(roleUpdateRequest.newRole());
                  return userRepository.save(user);
                })
            .map(userMapper::toDto)
            .orElseThrow(() -> UserNotFoundException.withId(userId));
    log.info("사용자 권한수정 완료: id={}", userId);
    invalidateUserSessions(userDto.username());
    return userDto;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public void invalidateUserSessions(String username) {
    List<Object> principals = sessionRegistry.getAllPrincipals();
    for (Object principal : principals) {
      if (principal instanceof DiscodeitUserDetails) {
        DiscodeitUserDetails user = (DiscodeitUserDetails) principal;
        if (user.getUsername().equals(username)) {
          for (SessionInformation sessionInfo : sessionRegistry.getAllSessions(principal, false)) {
            sessionInfo.expireNow();
          }
        }
      }
    }
  }

  @Override
  public boolean isUserOnline(String username) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof DiscodeitUserDetails)
        .map(principal -> (DiscodeitUserDetails) principal)
        .anyMatch(userDetails -> userDetails.getUsername().equals(username));
  }
}
