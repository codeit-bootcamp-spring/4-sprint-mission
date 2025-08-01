package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusDto createUserStatus(UUID userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("User with id " + userId + " not found"));

        boolean exists = userStatusRepository.findByUserId(userId).isPresent();
        if (exists) {
            throw new IllegalArgumentException("UserStatus with userId " + userId + " already exists");
        }

        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);
        return userStatusMapper.UserStatusToUserStatusDto(userStatus);
    }

    @Override
    public UserStatusDto findUserStatusById(UUID id) {
        UserStatus userStatus =
                userStatusRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException("UserStatus with id " + id + " not found"));
        return userStatusMapper.UserStatusToUserStatusDto(userStatus);
    }

    @Override
    public List<UserStatusDto> findAllUserStatus() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::UserStatusToUserStatusDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserStatusDto updateUserStatus(UserStatusUpdateDto userStatusUpdateDto) {
        UserStatus userStatus =
                userStatusRepository
                        .findById(userStatusUpdateDto.id())
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "UserStatus with id " + userStatusUpdateDto.id() + " not found"));

        applyUserStatusUpdate(userStatus, userStatusUpdateDto);

        UserStatus updated = userStatusRepository.save(userStatus);
        return userStatusMapper.UserStatusToUserStatusDto(updated);
    }

    @Override
    public UserStatusDto updateUserStatusByUserId(
            UUID userId, UserStatusUpdateDto userStatusUpdateDto) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        UserStatus userStatus =
                userStatusRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "UserStatus with userId " + userId + " not found"));

        applyUserStatusUpdate(userStatus, userStatusUpdateDto);

        UserStatus updated = userStatusRepository.save(userStatus);
        return userStatusMapper.UserStatusToUserStatusDto(updated);
    }

    @Override
    public void deleteUserStatusById(UUID id) {
        userStatusRepository
                .findById(id)
                .ifPresentOrElse(
                        userStatusRepository::delete,
                        () -> {
                            throw new IllegalArgumentException("UserStatus with id " + id + " not found");
                        });
    }

    private void applyUserStatusUpdate(UserStatus userStatus, UserStatusUpdateDto dto) {
        if (dto.newLastActiveAt() != null) {
            userStatus.setLastActiveAt(dto.newLastActiveAt());
        } else {
            userStatus.updateAccessTime();
        }
    }
}
