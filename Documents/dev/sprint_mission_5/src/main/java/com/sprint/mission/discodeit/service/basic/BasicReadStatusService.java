package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateDto dto) {
        if (!channelRepository.existsById(dto.getChannelId()) || !userRepository.existsById(dto.getUserId())) {
            throw new NoSuchElementException("해당 채널 데이터가 존재하지 않습니다.");
        }
        if(readStatusRepository.existsByChannelIdAndUserId(dto.getChannelId(), dto.getUserId())) {
            throw new IllegalArgumentException("이미 ReadStatus가 존재합니다.");
        }
        ReadStatus createdReadStatus = readStatusMapper.readStatusCreateDtoToReadStatus(dto);
        ReadStatus savedReadStatus = readStatusRepository.save(createdReadStatus);
        return readStatusMapper.readStatusToReadStatusResponseDto(savedReadStatus);
    }

    @Override
    public ReadStatusResponseDto find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 데이터가 존재하지 않습니다."));
        return readStatusMapper.readStatusToReadStatusResponseDto(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
            return readStatuses.stream()
                    .map(readStatusMapper::readStatusToReadStatusResponseDto)
                    .toList();
    }

    @Override
    public ReadStatusResponseDto update(ReadStatusUpdateDto dto) {
        ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(dto.getUserId(), dto.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("해당 데이터가 존재하지 않습니다."));

        readStatus.setReadAt(dto.getReadAt());
        readStatus.setUpdatedAt(Instant.now());
        ReadStatus updatedReadStatus = readStatusRepository.save(readStatus);
        return readStatusMapper.readStatusToReadStatusResponseDto(updatedReadStatus);
    }

    @Override
    public void delete(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 대상이 존재하지 않습니다."));
        readStatusRepository.deleteById(id);
    }
}
