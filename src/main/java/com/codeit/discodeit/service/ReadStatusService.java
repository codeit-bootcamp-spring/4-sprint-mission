package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.readstatus_dto.ReadStatusUpdateRequest;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  List<ReadStatus> findReadStatusesByUserId(UUID userId);

  ReadStatus createReadStatus(User user, Channel channel);

  ReadStatus updateReadStatusByReadStatusId(UUID readStatusId,
      ReadStatusUpdateRequest readStatusUpdateRequest);
}