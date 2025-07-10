package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto.*;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusResponse create(ReadStatusRequest readStatusRequest);

  ReadStatusResponse find(UUID id);

  List<ReadStatusResponse> findAllByUserId(UUID id);

  ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request);

  void delete(UUID id);

  void deleteAll();
}
