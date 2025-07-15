package com.codeit.discodeit.service.basic;


import com.codeit.discodeit.dto.readstatus_dto.ReadStatusUpdateRequest;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.exception.exception.DuplicateReadStatusException;
import com.codeit.discodeit.exception.exception.NoFindReadStatusException;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.ReadStatusRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatus createReadStatus(User user, Channel channel) {

    Optional<ReadStatus> readStatus = readStatusRepository.findReadStatusesByUserIdAndChannelId(
        user.getId(), channel.getId());
    if (readStatus.isPresent()) {
      throw new DuplicateReadStatusException("이미 읽음 상태가 존재함",
          "ReadStatus with userId {" + user.getId() + "} and channelId {" + channel.getId()
              + "} already exists");
    }

    ReadStatus newReadStatus = new ReadStatus(user.getId(), channel.getId()); // 생성자에 messageId 반영
    readStatusRepository.createReadStatus(newReadStatus);
    // 중복 검새해야하는디
    return newReadStatus;
  }

  @Override
  public List<ReadStatus> findReadStatusesByUserId(UUID userId) {

    return readStatusRepository.findReadStatusesByUserId(userId);
  }

  @Override
  public ReadStatus updateReadStatusByReadStatusId(UUID readStatusId,
      ReadStatusUpdateRequest readStatusUpdateRequest) {
    ReadStatus readStatus = findReadStatusByReadStatusId(readStatusId);

    if (readStatus == null) {
      throw new NoFindReadStatusException("Message 읽음 상태를 찾을 수 없음", readStatusId + " is not found");
    }

    readStatus.updateUpdatedAt();// 시간만 업데이트
    readStatus.setLastReadAt(readStatusUpdateRequest.getNewLastReadAt());
    readStatusRepository.updateReadStatus(readStatus);

    return readStatus;
  }

  private ReadStatus findReadStatusByReadStatusId(UUID readStatusId) {
    return readStatusRepository.findReadStatusesByReadStatusId(readStatusId)
        .orElseThrow(() -> new NoFindReadStatusException("해당하는 ReadStatus를 찾을 수 없습니다.",
            "ReadStatusId  {" + readStatusId + "} isn't exists"));
  }


}