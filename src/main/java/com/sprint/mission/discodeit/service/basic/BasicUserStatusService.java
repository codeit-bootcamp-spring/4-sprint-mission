package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusResponseDto create(UserStatusCreateDto userStatusCreateDto) {
        validateCreate(userStatusCreateDto);
        UserStatus userStatus = userStatusMapper.toEntity(userStatusCreateDto);
        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusResponseDto find(UUID id) {
        UserStatus userStatus = requireUserStatusById(id);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findAll() {
        List<UserStatus> userStatuses = userStatusRepository.findAll();
        return userStatusMapper.toDtoList(userStatuses);
    }

    @Override
    public UserStatusResponseDto update(UserStatusUpdateDto userStatusUpdateDto) {
        UserStatus userStatus = requireUserStatusById(userStatusUpdateDto.getId());
        // 사용자가 마지막으로 확인된 접속 시간 업데이트
        userStatusMapper.updateEntity(userStatusUpdateDto, userStatus);
        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusResponseDto updateByUserId(UUID userId) {
        UserStatus userStatus = requireUserStatusByUserId(userId);
        // 사용자가 마지막으로 확인된 접속 시간 업데이트
        userStatusMapper.updateEntity(new UserStatusUpdateDto(userStatus.getId()), userStatus);
        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public void delete(UUID id) {
        requireUserStatusById(id);
        userStatusRepository.deleteById(id);
    }

    /* =========================================================
     * INTERNAL VALIDATION HELPERS
     * ========================================================= */

    /**
     * 사용자 상태 생성 시 유효성 검사
     * - 사용자 존재 여부
     * - 중복 상태 존재 여부
     *
     * @param dto 사용자 상태 생성 DTO
     * @throws NoSuchElementException 사용자가 존재하지 않는 경우
     * @throws IllegalArgumentException 상태가 이미 존재하는 경우
     */
    private void validateCreate(UserStatusCreateDto dto) {
        validateUserExists(dto.getUserId());
        validateUserStatusNotExistsByUserId(dto.getUserId());
    }

    /**
     * 사용자 ID 존재 여부를 확인합니다.
     *
     * @param userId 사용자 ID
     * @throws NoSuchElementException 존재하지 않는 경우
     */
    private void validateUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("UserStatus with id " + userId + " not found");
        }
    }

    /**
     * 해당 사용자에 대한 UserStatus 정보가 이미 존재하는지 검사합니다.
     *
     * @param userId 사용자 ID
     * @throws IllegalArgumentException 이미 존재할 경우
     */
    private void validateUserStatusNotExistsByUserId(UUID userId) {
        if (userStatusRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("UserStatus already exists for userId " + userId);
        }
    }

    /**
     * 사용자 상태를 ID로 조회합니다.
     *
     * @param id 상태 ID
     * @return 사용자 상태 엔티티
     * @throws NoSuchElementException 존재하지 않는 경우
     */
    private UserStatus requireUserStatusById(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
    }

    /**
     * 사용자 ID로 사용자 상태를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 상태 엔티티
     * @throws NoSuchElementException 존재하지 않는 경우
     */
    private UserStatus  requireUserStatusByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus for userId " + userId + " not found"));
    }
}
