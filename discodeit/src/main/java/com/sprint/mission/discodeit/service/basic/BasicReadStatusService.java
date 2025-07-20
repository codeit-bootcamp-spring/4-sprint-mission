package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto.*;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusResponse create(ReadStatusRequest dto) {
        if (!userRepository.existsById(dto.userId())) {
            throw new NoSuchElementException("User not found: " + dto.userId());
        }

        if (!channelRepository.existsById(dto.channelId())) {
            throw new NoSuchElementException("Channel not found: " + dto.channelId());
        }

        boolean exists = readStatusRepository.findAllByUserId(dto.userId()).stream()
                .anyMatch(status -> status.getChannelId().equals(dto.channelId()));

        if (exists) {
            throw new IllegalArgumentException("ReadStatus already exists for user and channel");
        }

        ReadStatus newStatus = new ReadStatus(
                UUID.randomUUID(), // ID 직접 생성
                dto.userId(),
                dto.channelId(),
                dto.lastReadAt() != null
                        ? dto.lastReadAt()
                        : Instant.now()
        );

        ReadStatus saved = readStatusRepository.save(newStatus);
        return readStatusMapper.entityToReadStatusResponse(saved);
    }

    @Override
    public ReadStatusResponse find(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id: " + readStatusId));
        return readStatusMapper.entityToReadStatusResponse(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::entityToReadStatusResponse)
                .toList();
    }

    @Override
    public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest dto) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id: " + readStatusId));

        if (dto.newLastReadAt() != null) {
            readStatus.updateReadTime(dto.newLastReadAt());
        }

        ReadStatus updated = readStatusRepository.save(readStatus);

        return readStatusMapper.entityToReadStatusResponse(updated);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus not found with id: " + readStatusId);
        }

        readStatusRepository.deleteById(readStatusId);
    }
}
