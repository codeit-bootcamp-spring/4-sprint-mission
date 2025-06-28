package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusDto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.UserStatusDto.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusMapper userStatusMapper;
    private final UserStatusRepository userStatusRepository;

    //
    private final UserRepository userRepository;

    @Override
    public UserStatusResponseDto create(UserStatusCreateDto userStatusCreateDto) {
        // user 유효성 검사
        if (!userRepository.existsById(userStatusCreateDto.userId())) {
            throw new NoSuchElementException(this.getClass().getSimpleName() + ".create() 실패: User not found; userId가 잘못되었습니다.");
        }

        // userStatus 중복 생성 검사
        if (userStatusRepository.findByUserId(userStatusCreateDto.userId()).isPresent()) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".create() 실패: UserStatus는 같은 유저에 대해 중복으로 생성될 수 없습니다.");
        }

        UserStatus createdUserStatus = userStatusMapper.userStatusCreateDtoToUserStatus(userStatusCreateDto);
        userStatusRepository.save(createdUserStatus);
        return userStatusMapper.userStatusToUserStatusResponseDto(createdUserStatus);
    }

    @Override
    public UserStatusResponseDto find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(this.getClass().getSimpleName() + ".find() 실패: Invalid UserStatus id"));

        return userStatusMapper.userStatusToUserStatusResponseDto(userStatus);
    }

    @Override
    public UserStatusResponseDto findByUserId(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(this.getClass().getSimpleName() + ".findByUserId() 실패: Invalid User id"));

        return userStatusMapper.userStatusToUserStatusResponseDto(userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findAll() {
        List<UserStatusResponseDto> userStatusResponseDtos = new ArrayList<>();
        for (UserStatus userStatus : userStatusRepository.findAll()) {
            userStatusMapper.userStatusToUserStatusResponseDto(userStatus);
            userStatusResponseDtos.add(userStatusMapper.userStatusToUserStatusResponseDto(userStatus));
        }
        return userStatusResponseDtos;
    }

    @Override
    public UserStatusResponseDto update(UserStatusUpdateDto userStatusUpdateDto) {
        UserStatus updatedUserStatus = userStatusRepository.findById(userStatusUpdateDto.id())
                .orElseThrow(() -> new NoSuchElementException(this.getClass().getSimpleName() + ".update() 실패: Invalid UserStatus id"));

        updatedUserStatus.update(userStatusUpdateDto.newLastLoginTimes());
        userStatusRepository.save(updatedUserStatus);
        return userStatusMapper.userStatusToUserStatusResponseDto(updatedUserStatus);
    }

    @Override
    public UserStatusResponseDto updateByUserId(UserStatusUpdateDto userStatusUpdateDto, UUID userId) {
        UserStatus updatedUserStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(this.getClass().getSimpleName() + ".updateByUserId() 실패: Invalid Userid"));

        updatedUserStatus.update(userStatusUpdateDto.newLastLoginTimes());
        userStatusRepository.save(updatedUserStatus);
        return userStatusMapper.userStatusToUserStatusResponseDto(updatedUserStatus);
    }

    @Override
    public void delete(UUID id) {
        if (userStatusRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".delete() 실패: UserStatus not found; 이미 삭제되었거나 없습니다.");
        }
        userStatusRepository.delete(id);
    }
}
