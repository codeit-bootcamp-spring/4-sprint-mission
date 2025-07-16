package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatus;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  public ReadStatus createReadStatus(ReadStatusCreateRequest readStatusCreateRequest);

  public ReadStatus findReadStatus(UUID readStatusId);

  public List<ReadStatus> findAllReadStatusByUserId(UUID userId);

  public List<ReadStatus> findAllReadStatusByChannelId(UUID channelId);

  public ReadStatus updateReadStatus(UUID readStatusId,
      ReadStatusUpdateRequest readStatusUpdateRequest);

  public void deleteReadStatus(UUID readStatusId);
}
