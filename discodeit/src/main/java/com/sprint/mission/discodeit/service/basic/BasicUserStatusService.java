package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public UserStatusResponseDto create(UserStatusCreateDto dto) {
        if (!userRepository.existsById(dto.getUserId())) {
            throw new NoSuchElementException("User not found : " + dto.getUserId());
        }

        boolean userExists = userStatusRepository.findAll().stream()
                .anyMatch(userStatus -> userStatus.getUserId().equals(dto.getUserId()));
        if (userExists) {
            throw new IllegalStateException("User already exists : " + dto.getUserId());
        }
        UserStatus save = userStatusRepository.save(new UserStatus(dto.getUserId()));
        return userStatusMapper.entityToDto(save);
    }

    @Override
    public UserStatusResponseDto find(UUID id) {
        UserStatus userStatus = getUserStatusOrThrow(id);

        return userStatusMapper.entityToDto(userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserStatusResponseDto update(UserStatusUpdateDto dto) {
        UserStatus userStatus = getUserStatusOrThrow(dto.getId());
        userStatus.updateLastActiveAt();

        return userStatusMapper.entityToDto(userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusResponseDto updateByUserId(UUID userId) {
        UserStatus status = userStatusRepository.findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found : " + userId));
        status.updateLastActiveAt();

        return userStatusMapper.entityToDto(userStatusRepository.save(status));
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
