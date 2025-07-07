package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateDto readStatusCreateDto) {
        validateCreateReadStatus(readStatusCreateDto);
        ReadStatus readStatus = readStatusMapper.toEntity(readStatusCreateDto);
        readStatusRepository.save(readStatus);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public ReadStatusResponseDto find(UUID id) {
        ReadStatus readStatus = requireReadStatus(id);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        validateUserExists(userId);
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
        return readStatusMapper.toDtoList(readStatuses);
    }

    @Override
    public ReadStatusResponseDto update(ReadStatusUpdateDto readStatusUpdateDto) {
        ReadStatus readStatus = requireReadStatus(readStatusUpdateDto.getId());
        readStatusMapper.updateEntity(readStatusUpdateDto, readStatus);
        readStatusRepository.save(readStatus);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public void delete(UUID id) {
        requireReadStatus(id);
        readStatusRepository.deleteById(id);
    }

    /* =========================================================
     * INTERNAL VALIDATION HELPERS
     * ========================================================= */

    /**
     * ReadStatus 생성 시 유효성 검사:
     * - 채널 존재 여부
     * - 사용자 존재 여부
     * - 중복 ReadStatus 존재 여부
     *
     * @param dto 생성할 ReadStatus DTO
     */
    private void validateCreateReadStatus(ReadStatusCreateDto dto) {
        validateChannelExists(dto.getChannelId());
        validateUserExists(dto.getUserId());
        validateNotExists(dto.getUserId(), dto.getChannelId());
    }

    /**
     * 채널 ID가 존재하는지 검사합니다.
     *
     * @param channelId 채널 ID
     * @throws NoSuchElementException 채널이 존재하지 않는 경우
     */
    private void validateChannelExists(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }
    }

    /**
     * 유저 아이디가 존재하는지 검사합니다.
     *
     * @param userId 유저 ID
     * @throws NoSuchElementException 유저가 존재하지 않는 경우
     */
    private void validateUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found with id " + userId);
        }
    }

    /**
     * 동일한 사용자-채널 조합의 ReadStatus가 이미 존재하는지 검사합니다.
     *
     * @param userId 사용자 ID
     * @param channelId 채널 ID
     * @throws IllegalArgumentException 중복된 ReadStatus가 존재하는 경우
     */
    private void validateNotExists(UUID userId, UUID channelId) {
        if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new IllegalArgumentException(
                    "ReadStatus already exists for user " + userId +
                            " and channel " + channelId);
        }
    }

    /**
     * ID에 해당하는 ReadStatus를 조회합니다.
     *
     * @param id ReadStatus ID
     * @return ReadStatus 엔티티
     * @throws NoSuchElementException ReadStatus가 존재하지 않는 경우
     */
    private ReadStatus requireReadStatus(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + id + " not found"));
    }
}
