package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto.*;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusResponse create(UserStatusRequest statusRequest) {

        UUID userId = statusRequest.userId();

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found : " + statusRequest.userId());
        }

        boolean userExists = userStatusRepository.findAll().stream()
                .anyMatch(userStatus -> userStatus.getUserId().equals(statusRequest.userId()));
        if (userExists) {
            throw new IllegalStateException("User already exists : " + statusRequest.userId());
        }
        UserStatus save = userStatusRepository.save(new UserStatus(statusRequest.userId()));
        return userStatusMapper.toUserStatusResponse(save);
    }

    @Override
    public UserStatusResponse findById(UUID id) {
        UserStatus userStatus = getUserStatusOrThrow(id);

        return userStatusMapper.toUserStatusResponse(userStatus);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toUserStatusResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserStatusUpdateResponse update(UUID userId, UserStatusUpdateRequest updateRequest) {
        UserStatus status = getUserStatusOrThrow(userId);

        // 2. 상태 업데이트
        if (updateRequest.newLastActiveAt() != null) {
            status.updateLastActiveAt(updateRequest.newLastActiveAt());
        }

        userStatusRepository.save(status);

        return userStatusMapper.toUserStatusUpdateResponse(status);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId) {
        UserStatus status = userStatusRepository.findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found : " + userId));

        // 현재 시간으로 마지막 활동시간 업데이트
        status.updateLastActiveAt(Instant.now());

        UserStatus updated = userStatusRepository.save(status);

        return userStatusMapper.toUserStatusResponse(updated);

    }

    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new IllegalStateException("UserStatus not found : " + id);
        }
        userStatusRepository.delete(id);
    }

    private UserStatus getUserStatusOrThrow(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found : " + id));
    }
}
