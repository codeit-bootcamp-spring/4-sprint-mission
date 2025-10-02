package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.List;
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
@Transactional
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto updateUserRole(RoleUpdateRequest roleUpdateRequest) {
    log.debug("유저 권한 수정 시작");

    User userId = userRepository.findById(roleUpdateRequest
        .userId()).orElseThrow(() -> new UserNotFoundException());

    userId.updateRole(roleUpdateRequest.newRole());

    expireUserSession(userId.getEmail());
    log.debug("유저 권한 수정 완료");
    return userMapper.toDto(userId);

  }

  private void expireUserSession(String email){
    List<Object> principals = sessionRegistry.getAllPrincipals();
    for (Object principal : principals) {
      if (principal instanceof DiscodeitUserDetails userDetails &&
          userDetails.getUsername().equals(email)) {
        sessionRegistry.getAllSessions(principal, false)
            .forEach(SessionInformation::expireNow);
      }
    }
  }

}

