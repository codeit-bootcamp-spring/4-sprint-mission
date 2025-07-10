package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.dto.ReadStatusDto.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  public ReadStatusResponse create(ReadStatusRequest readStatusRequest) {

    UUID userId = readStatusRequest.userId();
    UUID channelId = readStatusRequest.channelId();
    // Throw error if channel or user does not exist
    if (!channelRepository.existsById(channelId)) {
      throw new IllegalArgumentException("Channel not found");
    }
    if (!userRepository.existsById(userId)) {
      throw new IllegalArgumentException("User not found");
    }

    // Throw error if readstatus already exists
    Optional<ReadStatus> readStatus = readStatusRepository.findByChannelAndUserId(
        channelId, userId
    );
    if (readStatus.isPresent()) {
      throw new IllegalArgumentException("Read status already exists");
    }

    ReadStatus newReadStatus = new ReadStatus(userId, channelId);
    return readStatusMapper.toReadStatusResponse(readStatusRepository.save(newReadStatus));
  }

  @Override
  public ReadStatusResponse find(UUID id) {
    return readStatusRepository.findById(id)
        .map(readStatusMapper::toReadStatusResponse)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
  }

  @Override
  public List<ReadStatusResponse> findAllByUserId(UUID id) {
    return readStatusRepository.findByUserId(id).stream()
        .map(readStatusMapper::toReadStatusResponse)
        .toList();
  }

  @Override
  public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));

    readStatus.update(newLastReadAt);
    return readStatusMapper.toReadStatusResponse(readStatusRepository.save(readStatus));
  }

  @Override
  public void delete(UUID id) {
    if (!readStatusRepository.existsById(id)) {
      throw new NoSuchElementException("ReadStatus with id " + id + " not found");
    }
    readStatusRepository.deleteById(id);
  }

  @Override
  public void deleteAll() {
    readStatusRepository.deleteAll();
  }
}
