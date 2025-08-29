package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user_status.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user_status.UserStatusInvalidException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserStatus create(User user) {
        validateUser(user.getId());
        existsUserStatus(user.getId());

        UserStatus userStatus = new UserStatus(user, Instant.now());
        userStatusRepository.save(userStatus);
        user.setStatus(userStatus);
        return userStatus;
    }

    @Transactional
    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateRequest) {
        Instant newLastActiveAt = userStatusUpdateRequest.newLastActiveAt();
        UserStatus findUserStatus = userStatusRepository.findByUserId(userId);
        if (findUserStatus.getLastActiveAt().isAfter(newLastActiveAt))
            throw new UserStatusInvalidException(ErrorCode.INVALID_PAST_TIME, Map.of("newLastActiveAt", newLastActiveAt));

        findUserStatus.setLastActiveAt(newLastActiveAt);

        userStatusRepository.save(findUserStatus);
        return findUserStatus;
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }

    private void validateUser(UUID userId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
    }

    private void existsUserStatus(UUID userId) {
        if (userStatusRepository.existsByUser_Id(userId))
            throw new UserStatusAlreadyExistsException(ErrorCode.USER_ALREADY_EXISTS_USERSTATUS, Map.of("userId", userId));
    }
}
