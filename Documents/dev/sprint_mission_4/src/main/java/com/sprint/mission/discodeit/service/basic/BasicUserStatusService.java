package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
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

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserStatusMapper userStatusMapper;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponseDto create(UserStatusCreateDto dto) {
        if (!userRepository.existsById(dto.getUserId())) {
            throw new NoSuchElementException("유저 아이디 " + dto.getUserId() + " 에 해당하는 유저를 찾지 못했습니다.");
        }
        if (userStatusRepository.existsByUserId(dto.getUserId())) {
            throw new NoSuchElementException("이미 해당 유저의 상태 정보가 존재합니다.");
        }
        UserStatus createdUserStatus = userStatusMapper.userStatusCreateDtoToUserStatus(dto);
        UserStatus savedUserStatus = userStatusRepository.save(createdUserStatus);
        return userStatusMapper.userStatusToUserStatusResponseDto(savedUserStatus);
    }

    @Override
    public UserStatusResponseDto find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 데이터가 존재하지 않습니다."));
        return userStatusMapper.userStatusToUserStatusResponseDto(userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findAllByUserId(UUID userId) {
        List<UserStatus> UserStatuses = userStatusRepository.findAllByUserId(userId);
        return UserStatuses.stream()
                .map(userStatusMapper::userStatusToUserStatusResponseDto)
                .toList();
    }

    @Override
    public UserStatusResponseDto update(UserStatusUpdateDto dto) {
        UserStatus userStatus = userStatusRepository.findById(dto.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 데이터가 존재하지 않습니다."));

        userStatus.setLastAccessedAt(Instant.now());
        UserStatus updatedUserStatus = userStatusRepository.save(userStatus);
        return userStatusMapper.userStatusToUserStatusResponseDto(updatedUserStatus);
    }

    @Override
    public UserStatusResponseDto updateByUserId(UserStatusUpdateDto dto) {
        List<UserStatus> userStatuses = userStatusRepository.findAllByUserId(dto.getUserId());
        if (!userStatuses.isEmpty()) {
            throw new NoSuchElementException("해당 유저의 상태 정보가 존재하지 않습니다.");
        }
        UserStatus userStatus = userStatuses.get(0);
        userStatus.setLastAccessedAt(Instant.now());
        UserStatus updatedUserStatus = userStatusRepository.save(userStatus);
        return userStatusMapper.userStatusToUserStatusResponseDto(updatedUserStatus);
    }

    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 대상이 존재하지 않습니다."));
        userStatusRepository.deleteById(id);
    }
}
