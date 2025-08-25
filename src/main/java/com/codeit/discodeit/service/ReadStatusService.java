package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.readstatus_dto.ReadStatusDto;
import com.codeit.discodeit.dto.readstatus_dto.ReadStatusUpdateRequest;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  List<ReadStatusDto> findReadStatuseDtoListByUserId(UUID userId);
  ReadStatusDto updateReadStatusByReadStatusId(UUID readStatusId,
      ReadStatusUpdateRequest readStatusUpdateRequest);

  ReadStatusDto findReadStatusByUserIdAndChannelId(UUID userId, UUID channelId);
  void createReadStatus(User user, Channel channel);

  List<ReadStatus> findReadStatusesByChannelId(Channel channel);
  List<ReadStatus> findReadStatusesByUserId(UUID userId);
}