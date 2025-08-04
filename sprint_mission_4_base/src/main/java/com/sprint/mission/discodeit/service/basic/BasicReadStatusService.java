package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.maper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " does not exist");
        }
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
        }
        if (readStatusRepository.findAllByUserId(userId).stream()
                .anyMatch(readStatus -> readStatus.getChannelId().equals(channelId))) {
            throw new IllegalArgumentException("ReadStatus with userId " + userId + " and channelId " + channelId + " already exists");
        }

        ReadStatus readStatus = readStatusMapper.readStatusCreateRequestToReadStatus(request);
        readStatusRepository.save(readStatus);

        return readStatusMapper.readStatusToReadStatusResponseDto(readStatus);
    }

    @Override
    public ReadStatusResponseDto find(UUID readStatusId) {

        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
        return readStatusMapper.readStatusToReadStatusResponseDto(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::readStatusToReadStatusResponseDto)
                .toList();
    }

    @Override
    public ReadStatusResponseDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        Instant newLastReadAt = request.newLastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));

        readStatus.update(newLastReadAt);
        readStatusRepository.save(readStatus);

        return readStatusMapper.readStatusToReadStatusResponseDto(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        }
        readStatusRepository.deleteById(readStatusId);
    }
}
