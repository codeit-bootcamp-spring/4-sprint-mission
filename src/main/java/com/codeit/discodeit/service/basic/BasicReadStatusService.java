package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.readstatus_dto.ReadStatusUpdateRequest;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.exception.ErrorCode;
import com.codeit.discodeit.exception.exception.BusinessException;
import com.codeit.discodeit.repository.ReadStatusRepository;
import com.codeit.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;

  @Override
  @Transactional
  public void createReadStatus(User user, Channel channel){
    ReadStatus readStatus = new ReadStatus();
    readStatus.setChannel(channel);
    readStatus.setUser(user);
    readStatusRepository.createReadStatus(readStatus);
  }

  @Override
  @Transactional
  public ReadStatus updateReadStatusByReadStatusId(UUID readStatusId,
      ReadStatusUpdateRequest readStatusUpdateRequest) {
    ReadStatus readStatus = findReadStatusByReadStatusId(readStatusId);

    if (readStatus == null) {
      throw new BusinessException(ErrorCode.NO_FIND_READ_STATUS);
    }

    readStatus.setLastReadAt(readStatusUpdateRequest.getNewLastReadAt());
    readStatusRepository.updateReadStatus(readStatus);

    return readStatus;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatus> findReadStatusesByUserId(UUID userId) {
    return readStatusRepository.findReadStatusesByUserId(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatus findReadStatusByUserIdAndChannelId(UUID userId, UUID channelId) {
    Optional<ReadStatus> readStatus = readStatusRepository.findReadStatusesByUserIdAndChannelId(userId, channelId);
    if (readStatus.isEmpty()) {
      throw new BusinessException(ErrorCode.NO_FIND_READ_STATUS);
    }

    return readStatus.get();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatus> findReadStatusesByChannelId(Channel channel){
    return readStatusRepository.findReadStatusByChannel(channel);
  }

  private ReadStatus findReadStatusByReadStatusId(UUID readStatusId) {
    return readStatusRepository.findReadStatusesByReadStatusId(readStatusId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NO_FIND_READ_STATUS));
  }
}