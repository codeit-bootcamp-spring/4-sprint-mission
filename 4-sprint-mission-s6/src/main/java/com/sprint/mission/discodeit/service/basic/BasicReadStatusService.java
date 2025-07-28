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
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException("User with id " + userId + " does not exist");
    }
    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
    }
    boolean exists = readStatusRepository.findAllByUserId(userId).stream()
            .anyMatch(readStatus -> readStatus.getChannel().getId().equals(channelId));
    if (exists) {
      throw new IllegalArgumentException(
          "ReadStatus with userId " + userId + " and channelId " + channelId + " already exists");
    }

    ReadStatus readStatus = readStatusMapper.toEntity(request);

    User user = userRepository.getReferenceById(userId);
    Channel channel = channelRepository.getReferenceById(channelId);
    readStatus.setUser(user);
    readStatus.setChannel(channel);

    ReadStatus save = readStatusRepository.save(readStatus);
    return readStatusMapper.toDto(save);
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatusDto find(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(
                    () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));

      return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    List<ReadStatus> list = readStatusRepository.findAllByUserId(userId).stream()
            .toList();

    return list.stream()
            .map(readStatusMapper::toDto)
            .toList();
  }

  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));

    readStatusMapper.updateFromRequest(request, readStatus);

    ReadStatus updated = readStatusRepository.save(readStatus);

    return readStatusMapper.toDto(updated);
  }

  @Override
  public void delete(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));

    readStatusRepository.delete(readStatus);
  }
}
