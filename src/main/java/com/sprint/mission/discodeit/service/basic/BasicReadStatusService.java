package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateDto readStatusCreateDto) {
        isExistReadStatusByIds(readStatusCreateDto.getChannelId(), readStatusCreateDto.getUserId());
        ReadStatus readStatus = new ReadStatus(readStatusCreateDto.getUserId(), readStatusCreateDto.getChannelId());
        return readStatusMapper.ReadStatusToReadStatusResponseDto(readStatusRepository.save(readStatus));
    }

    @Override
    public ReadStatusResponseDto findById(UUID readStatusId) {
        Optional<ReadStatus> readStatus = readStatusRepository.findById(readStatusId);

        if (readStatus.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 ID 입니다.");
        }

        return readStatusMapper.ReadStatusToReadStatusResponseDto(readStatus.get());
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);

        return readStatuses.stream()
                .map(readStatusMapper::ReadStatusToReadStatusResponseDto)
                .toList();
    }

    @Override
    public ReadStatusResponseDto update(ReadStatusUpdateDto readStatusUpdateDto) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusUpdateDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 ID 입니다."));

        readStatus.update(readStatusUpdateDto.getLastReadAt());
        readStatusRepository.save(readStatus);

        return readStatusMapper.ReadStatusToReadStatusResponseDto(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new IllegalArgumentException("유효하지 않은 ID 입니다.");
        }

        readStatusRepository.deleteById(readStatusId);
    }

    private void isExistReadStatusByIds(UUID channelId, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        if (readStatusRepository.findByChannelIdAndUserId(channelId, userId).isPresent()) {
            throw new IllegalArgumentException("이미 만들어진 채널입니다.");
        }

    }
}
