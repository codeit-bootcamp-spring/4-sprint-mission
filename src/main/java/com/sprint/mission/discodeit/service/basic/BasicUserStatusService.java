package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusPostDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusResponseDto create(UserStatusPostDto userStatusPostDto) {
        validateUser(userStatusPostDto.userId());
        existsUserStatus(userStatusPostDto.userId());

        UserStatus userStatus = new UserStatus(userStatusPostDto.userId(), userStatusPostDto.latestActiveAt(), true);
        userStatusRepository.save(userStatus);

        return userStatusMapper.toUserStatusResponseDto(userStatus);
    }

    @Override
    public UserStatusResponseDto find(UUID id) {
        return userStatusMapper.toUserStatusResponseDto(userStatusRepository.findById(id));
    }

    @Override
    public UserStatusResponseDto findByUserId(UUID userId) {
        return userStatusMapper.toUserStatusResponseDto(userStatusRepository.findByUserId(userId));
    }

    @Override
    public List<UserStatusResponseDto> findAll() {
        List<UserStatusResponseDto> userStatusResponseDtos = new ArrayList<>();
        userStatusRepository.findAll().stream()
                .forEach(userStatus -> userStatusResponseDtos.add(userStatusMapper.toUserStatusResponseDto(userStatus)));
        return userStatusResponseDtos;
    }

    @Override
    public UserStatusResponseDto update(UUID id, UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatus findUserStatus = userStatusRepository.findById(id);

        if (findUserStatus.getLastActiveAt().isAfter(userStatusUpdateRequest.newLastActivaAt()))
            throw new IllegalArgumentException("새로운 기록이 기존 기록보다 이전입니다.");

        Optional.ofNullable(userStatusUpdateRequest.newLastActivaAt()).ifPresent(findUserStatus::setLastActiveAt);
        findUserStatus.setUpdatedAt(userStatusUpdateRequest.newLastActivaAt());

        userStatusRepository.save(findUserStatus);
        return userStatusMapper.toUserStatusResponseDto(findUserStatus);
    }

    @Override
    public UserStatusResponseDto updateByUserId(UUID userId) {
        Instant latestActiveAt = Instant.now();
        UserStatus findUserStatus = userStatusRepository.findByUserId(userId);
        if (findUserStatus.getLastActiveAt().isAfter(latestActiveAt))
            throw new IllegalArgumentException("새로운 기록이 기존 기록보다 이전입니다.");

        findUserStatus.setLastActiveAt(latestActiveAt);
        findUserStatus.setUpdatedAt(latestActiveAt);

        userStatusRepository.save(findUserStatus);
        return userStatusMapper.toUserStatusResponseDto(findUserStatus);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.delete(id);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        userStatusRepository.deleteByUserId(userId);
    }

    private void validateUser(UUID userId) {
        if (!userRepository.findAll().stream()
                .anyMatch(user -> user.getId().equals(userId))) throw new IllegalArgumentException("User not found");
    }

    private void existsUserStatus(UUID userId) {
        if (!userStatusRepository.findAll().stream()
                .anyMatch(userStatus -> userStatus.getUserId().equals(userId)))
            throw new IllegalStateException("User already exists.");
    }
}
