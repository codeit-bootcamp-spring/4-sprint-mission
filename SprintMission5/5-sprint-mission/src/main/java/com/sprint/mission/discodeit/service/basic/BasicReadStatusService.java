package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatus;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  public ReadStatus createReadStatus(ReadStatusCreateRequest readStatusCreateRequest) {
    if (readStatusCreateRequest.channelId() == null || readStatusCreateRequest.userId() == null) {
      throw new IllegalArgumentException("user와 channel은 필수입니다.");
    }

    userRepository
        .findById(readStatusCreateRequest.userId())
        .orElseThrow(() -> new IllegalArgumentException("해당 user는 존재하지 않습니다."));
    channelRepository
        .findById(readStatusCreateRequest.channelId())
        .orElseThrow(() -> new IllegalArgumentException("해당 channel은 존재하지 않습니다."));

    boolean exists =
        readStatusRepository.findAll().stream()
            .anyMatch(
                rs ->
                    rs.getUserId().equals(readStatusCreateRequest.userId())
                        && rs.getChannelId().equals(readStatusCreateRequest.channelId()));
    if (exists) {
      throw new IllegalArgumentException("이미 해당 채널에 읽음 상태가 존재합니다.");
    }

    ReadStatusEntity readStatusEntity = readStatusMapper.readStatusCreateDtoToReadStatus(
        readStatusCreateRequest);

    return readStatusMapper.toReadStatusResponse(readStatusRepository.save(readStatusEntity));
  }

  @Override
  public ReadStatus findReadStatus(UUID readStatusId) {
    ReadStatusEntity readStatusEntity =
        readStatusRepository
            .findById(readStatusId)
            .orElseThrow(() -> new IllegalArgumentException("readStatus가 존재하지 않습니다."));
    return readStatusMapper.toReadStatusResponse(readStatusEntity);
  }

  @Override
  public List<ReadStatus> findAllReadStatusByUserId(UUID userId) {
    return readStatusRepository.findAll().stream()
        .filter(readStatus -> readStatus.getUserId().equals(userId))
        .map(readStatusMapper::toReadStatusResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<ReadStatus> findAllReadStatusByChannelId(UUID channelId) {
    return readStatusRepository.findAll().stream()
        .filter(readStatus -> readStatus.getChannelId().equals(channelId))
        .map(readStatusMapper::toReadStatusResponse)
        .collect(Collectors.toList());
  }

  @Override
  public ReadStatus updateReadStatus(UUID readStatusId,
      ReadStatusUpdateRequest readStatusUpdateRequest) {
    ReadStatusEntity readStatusEntity =
        readStatusRepository
            .findById(readStatusId)
            .orElseThrow(() -> new IllegalArgumentException("ReadStatus를 찾지 못했습니다."));

    readStatusEntity.updateReadTime(readStatusUpdateRequest.newLastReadAt());
    readStatusRepository.save(readStatusEntity);

    return readStatusMapper.toReadStatusResponse(readStatusEntity);
  }

  @Override
  public void deleteReadStatus(UUID readStatusId) {
    readStatusRepository
        .findById(readStatusId)
        .ifPresentOrElse(
            binaryContent -> {
              readStatusRepository.delete(readStatusId);
            },
            () -> {
              throw new IllegalArgumentException("삭제할 binaryContent가 없습니다.");
            });
  }
}
