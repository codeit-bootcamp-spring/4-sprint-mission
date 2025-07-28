package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.Objects;
import lombok.Locked.Read;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;
  private final UserStatusRepository userStatusRepository;

  @Override
  @Transactional
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    if (!userRepository.existsById(request.userId())) {
      throw new NoSuchElementException("User with id " + request.userId() + " does not exist");
    }
    if(!channelRepository.existsById(request.channelId())) {
      throw new NoSuchElementException("Channel with id " + request.channelId() + " does not exist");
    }
    if (readStatusRepository.findAllByUserId(request.userId()).stream()
        .anyMatch(readStatus -> readStatus.getChannel().getId().equals(request.channelId()))) {
      throw new IllegalArgumentException(
          "ReadStatus with userId " + request.userId() + " and channelId " + request.channelId() + " already exists");
    }

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new NoSuchElementException("User with id " + request.userId() + " not found"));
    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + request.channelId() + " not found"));

    ReadStatus readStatus = readStatusMapper.mapToCreateEntity(request, user, channel);

    return readStatusMapper.mapToDto(readStatusRepository.save(readStatus));
  }

  @Override
  public ReadStatus find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    if(!userRepository.existsById(userId)) {
      throw new NoSuchElementException("User with id " + userId + " does not exist");
    }

    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);

    return readStatusMapper.mapToDtoList(readStatuses);
  }

  @Override
  @Transactional
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(()-> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
    if (request.newLastReadAt() != null && request.newLastReadAt().isAfter(Instant.now())) {
      throw new IllegalArgumentException("New last read at cannot be in the future");
    }

    if (request.newLastReadAt() != null && !Objects.equals(readStatus.getLastReadAt(),
        request.newLastReadAt())) {
      readStatus.setLastReadAt(request.newLastReadAt());
    }
    readStatusRepository.save(readStatus);

    return readStatusMapper.mapToDto(readStatus);
  }

  @Override
  public void delete(UUID readStatusId) {
    if (!readStatusRepository.existsById(readStatusId)) {
      throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
    }
    readStatusRepository.deleteById(readStatusId);
  }
}
