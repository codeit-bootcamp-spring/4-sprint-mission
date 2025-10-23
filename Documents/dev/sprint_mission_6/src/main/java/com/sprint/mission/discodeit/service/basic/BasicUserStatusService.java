package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public UserStatusDto create(UserStatusCreateDto dto) {
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

    @Transactional(readOnly = true)
    @Override
    public UserStatusDto find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 데이터가 존재하지 않습니다."));
        return userStatusMapper.userStatusToUserStatusResponseDto(userStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserStatusDto> findAllByUserId(UUID userId) {
        List<UserStatus> UserStatuses = userStatusRepository.findAllByUserId(userId);
        return UserStatuses.stream()
                .map(userStatusMapper::userStatusToUserStatusResponseDto)
                .toList();
    }

    @Transactional
    @Override
    public UserStatusDto update(UserStatusUpdateDto dto) {
        UserStatus userStatus = userStatusRepository.findById(dto.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 데이터가 존재하지 않습니다."));

        userStatus.setLastActiveAt(Instant.now());
        return userStatusMapper.userStatusToUserStatusResponseDto(userStatus);
    }

    @Transactional
    @Override
    public UserStatusDto updateByUserId(UserStatusUpdateDto dto) {
        UserStatus userStatus = userStatusRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("해당 유저의 상태 정보가 존재하지 않습니다."));
        userStatus.setLastActiveAt(Instant.now());
        return userStatusMapper.userStatusToUserStatusResponseDto(userStatus);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 대상이 존재하지 않습니다."));
        userStatusRepository.deleteById(id);
    }
}
