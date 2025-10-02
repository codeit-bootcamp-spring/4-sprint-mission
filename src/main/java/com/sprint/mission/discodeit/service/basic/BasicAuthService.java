package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.constant.UserErrorCode;
import com.sprint.mission.discodeit.dto.auth.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicAuthService implements AuthService {

    @Value("${admin.username}")
    private String adminName;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SessionRegistry sessionRegistry;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse updateRole(RoleUpdateRequest roleUpdateRequest) {
        User user = userRepository.findById(roleUpdateRequest.getUserId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.updateRole(roleUpdateRequest.getNewRole());

        UserResponse userResponse = userMapper.toResponse(user);

        DiscodeitUserDetails newDetails = new DiscodeitUserDetails(userResponse, user.getPassword());

        sessionRegistry.getAllPrincipals().stream()
                        .filter(principal -> principal instanceof DiscodeitUserDetails)
                        .filter(principal -> ((DiscodeitUserDetails) principal).getUserResponse().getId().equals(user.getId()))
                        .findFirst()
                        .ifPresent(
                                principal ->
                                {
                                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                                    sessions.forEach(SessionInformation::expireNow);
                                }
                        );

        userRepository.save(user);
        return userMapper.toResponse(user);

    }

    @Transactional
    public void registerAdmin() {
        Optional<User> existAdmin = userRepository.findByEmail(adminEmail);

        if(existAdmin.isPresent()) {
            return;
        }

        User newAdmin = User.builder()
                .username(adminName)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();

        userRepository.save(newAdmin);
    }

}
