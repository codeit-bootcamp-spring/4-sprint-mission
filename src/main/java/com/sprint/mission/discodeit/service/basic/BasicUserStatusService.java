package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusResponseDto create(UserStatusCreateDto userStatusCreateDto) {
        User user = userRepository.findById(userStatusCreateDto.getUserId())
                .orElseThrow(()-> new IllegalArgumentException("유효하지 않은 유저입니다."));

        isExistUserStatusByUserId(user.getId());

        UserStatus userStatus = userStatusMapper.UserStatusCreateDtoToUserStatus(userStatusCreateDto);
        userStatusRepository.save(userStatus);

        return userStatusMapper.UserStatusToUserStatusResponseDto(userStatus);
    }

    @Override
    public UserStatusResponseDto findById(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId).orElseThrow(
                () -> new IllegalArgumentException("유효하지 않은 ID 입니다.")
        );

        return userStatusMapper.UserStatusToUserStatusResponseDto(userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findByAll() {
        List<UserStatus> userStatuses = userStatusRepository.findAll();
        return userStatuses.stream()
                .map(userStatusMapper::UserStatusToUserStatusResponseDto)
                .toList();
    }

    @Override
    public UserStatusResponseDto update(UserStatusUpdateDto userStatusUpdateDto) {
        UserStatus userStatus = userStatusRepository.findById(userStatusUpdateDto.getId()).orElseThrow(
                () -> new IllegalArgumentException("유효하지 않은 ID 입니다.")
        );

        userStatus.update(userStatusUpdateDto.getLoginType());
        userStatusRepository.save(userStatus);

        return userStatusMapper.UserStatusToUserStatusResponseDto(userStatus);
    }

    @Override
    public UserStatusResponseDto updateByUserId(UserStatusUpdateDto userStatusUpdateDto) {
        UserStatus userStatus = userStatusRepository.findByUserId(userStatusUpdateDto.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("유효하지 않은 ID 입니다.")
        );

        userStatus.update(userStatusUpdateDto.getLoginType());
        userStatusRepository.save(userStatus);

        return userStatusMapper.UserStatusToUserStatusResponseDto(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new IllegalArgumentException("유효하지 않은 ID 입니다.");
        }

        userStatusRepository.deleteById(userStatusId);
    }

    private void isExistUserStatusByUserId(UUID userId) {
       if(userStatusRepository.existsById(userId)){
           throw new IllegalArgumentException("이미 유저 상태가 존재합니다.");
       }
    }
}
