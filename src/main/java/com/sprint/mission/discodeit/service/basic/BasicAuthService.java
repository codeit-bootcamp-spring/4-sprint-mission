package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetailService;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;
    private final DiscodeitUserDetailService userDetailService;

    @Transactional
    @Override
    public UserDto updateUserRole(UserRoleUpdateRequest userRoleUpdateRequest) {
        log.debug("사용자 권한 수정 시작: request={}", userRoleUpdateRequest);
        UUID userId = userRoleUpdateRequest.userId();
        Role newRole = userRoleUpdateRequest.newRole();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        user.updateRole(newRole);
        expireUserSession(user.getUsername());
        boolean online = isLoggedInByUserId(userId);
        log.info("사용자 권한 수정 완료: userId={}", userId);
        return userMapper.toDto(user, online);
    }

    @Override
    public boolean isLoggedInByUserId(UUID userId) {
        User user = getUserByUserId(userId);
        DiscodeitUserDetails userDetails = userDetailService.loadUserByUsername(user.getUsername());

        List<SessionInformation> sessions =
                sessionRegistry.getAllSessions(userDetails, false);

        return sessions.stream().anyMatch(s -> !s.isExpired());
    }

    private User getUserByUserId(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
    }

    private void expireUserSession(String username) {
        DiscodeitUserDetails userDetails = userDetailService.loadUserByUsername(username);
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
        for (SessionInformation session : sessions) {
            session.expireNow();
        }
    }
}
