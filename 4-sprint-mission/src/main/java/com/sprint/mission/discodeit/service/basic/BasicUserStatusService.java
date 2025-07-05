package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusResponseDto createUserStatus(UserStatusCreateDto userStatusCreateDto) {
        userRepository
                .findById(userStatusCreateDto.userId())
                .orElseThrow(() -> new IllegalArgumentException("user가 존재하지 않습니다."));

        boolean exists =
                userStatusRepository.findAll().stream()
                        .anyMatch(us -> us.getUserId().equals(userStatusCreateDto.userId()));
        if (exists) {
            throw new IllegalArgumentException("userStatus가 이미 존재합니다.");
        }

        UserStatus userStatus = userStatusMapper.UserStatusCreateDtoToUserStatus(userStatusCreateDto);
        return userStatusMapper.UserStatusToUserStatusResponseDto(
                userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusResponseDto findUserStatusById(UUID id) {
        UserStatus userStatus =
                userStatusRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("userStatus가 존재하지 않습니다."));
        return userStatusMapper.UserStatusToUserStatusResponseDto(userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findAllUserStatus() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::UserStatusToUserStatusResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserStatusResponseDto updateUserStatus(UserStatusUpdateDto userStatusUpdateDto) {
        UserStatus userStatus =
                userStatusRepository
                        .findById(userStatusUpdateDto.id())
                        .orElseThrow(() -> new IllegalArgumentException("userStatus가 존재하지 않습니다."));
        userStatus.updateAccessTime();
        userStatusRepository.save(userStatus);
        return userStatusMapper.UserStatusToUserStatusResponseDto(userStatus);
    }

    @Override
    public UserStatusResponseDto updateUserStatusByUserId(UUID userId) {
        UUID userStatusId = getUserStatusId(userId);
        userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user가 존재하지 않습니다."));

        UserStatus userStatus =
                userStatusRepository
                        .findById(userStatusId)
                        .orElseThrow(() -> new IllegalArgumentException("userStatus가 존재하지 않습니다."));

        userStatus.updateAccessTime();
        userStatusRepository.save(userStatus);

        return userStatusMapper.UserStatusToUserStatusResponseDto(userStatus);
    }

    @Override
    public void deleteUserStatusById(UUID id) {
        userStatusRepository
                .findById(id)
                .ifPresentOrElse(
                        userStatus -> {
                            userStatusRepository.delete(id);
                        },
                        () -> {
                            throw new IllegalArgumentException("삭제할 userStatus가 없습니다.");
                        });
    }

    private UUID getUserStatusId(UUID userId) {
        return userStatusRepository.findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .map(UserStatus::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("userStatus가 존재하지 않습니다."));
    }
}
