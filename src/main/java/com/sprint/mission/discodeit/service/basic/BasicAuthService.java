package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;

    @Transactional
    @Override
    @PreAuthorize("hasRole=('ADMIN')")
    public User updateRole(RoleUpdateRequest roleUpdateRequest) {
        User user = getValidUser(roleUpdateRequest.userId());
        log.info("인가 권한 변경 id = {}, oldRole = {}, newRole = {}", roleUpdateRequest.userId(), user.getRole(), roleUpdateRequest.newRole());
        user.updateRole(roleUpdateRequest.newRole());
        sessionRegistry.getAllPrincipals()
                .forEach(principal -> {
                    if (principal instanceof DiscodeitUserDetails) {
                        DiscodeitUserDetails discodeitUserDetails = (DiscodeitUserDetails) principal;
                        if (discodeitUserDetails.getUserDto().id().equals(user.getId())) {
                            List<SessionInformation> info = sessionRegistry.getAllSessions(principal, false);
                            info.forEach(sessionInformation -> {
                                log.debug("세션 만료 sessionId = {}", sessionInformation.getSessionId());
                                sessionInformation.expireNow();
//                                sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
                            });
                            log.debug("세션 {}개 무효화", info.size());
                        }
                    }
                });
        return userRepository.save(user);
    }

    private User getValidUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("해당 유저를 찾을 수 없음 id = {}", userId);
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
        });
    }
}
