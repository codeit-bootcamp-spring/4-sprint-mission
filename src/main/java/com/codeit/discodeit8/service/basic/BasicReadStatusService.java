package com.codeit.discodeit8.service.basic;

import com.codeit.discodeit8.dto.readstatus_dto.ReadStatusDto;
import com.codeit.discodeit8.dto.readstatus_dto.ReadStatusUpdateRequest;
import com.codeit.discodeit8.entity.Channel;
import com.codeit.discodeit8.entity.ReadStatus;
import com.codeit.discodeit8.entity.User;
import com.codeit.discodeit8.exception.readstatus.ReadStatusNotFoundException;
import com.codeit.discodeit8.mapper.ReadStatusMapper;
import com.codeit.discodeit8.repository.ReadStatusRepository;
import com.codeit.discodeit8.service.ReadStatusService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  @Transactional
  public void createReadStatus(User user, Channel channel) {
    ReadStatus readStatus = new ReadStatus();
    readStatus.setChannel(channel);
    readStatus.setUser(user);
    readStatusRepository.save(readStatus);
  }

  @Override
  @Transactional
  public ReadStatusDto updateReadStatusByReadStatusId(UUID readStatusId,
      ReadStatusUpdateRequest readStatusUpdateRequest) {
    ReadStatus readStatus = findReadStatusByReadStatusId(readStatusId);

    if (readStatus == null) {
      Map<String, Object> details = Map.of(
          "이유", "읽기 상태 없음"
      );
      throw new ReadStatusNotFoundException(details);
    }

    readStatus.setLastReadAt(readStatusUpdateRequest.getNewLastReadAt());
    readStatusRepository.save(readStatus);

    return readStatusMapper.toReadStatusDto(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDto> findReadStatuseDtoListByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toReadStatusDto).collect(
            Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatus> findReadStatusesByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatusDto findReadStatusByUserIdAndChannelId(UUID userId, UUID channelId) {
    Optional<ReadStatus> readStatus = readStatusRepository.findByUserIdAndChannelId(userId,
        channelId);
    if (readStatus.isEmpty()) {
      Map<String, Object> details = Map.of(
          "이유", "읽기 상태 없음"
      );
      throw new ReadStatusNotFoundException(details);
    }

    return readStatusMapper.toReadStatusDto(readStatus.get());
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatus> findReadStatusesByChannelId(Channel channel) {
    return readStatusRepository.findByChannel(channel);
  }

  private ReadStatus findReadStatusByReadStatusId(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          Map<String, Object> details = Map.of(
              "이유", "읽기 상태 없음"
          );
          return new ReadStatusNotFoundException(details);
        });
  }
}