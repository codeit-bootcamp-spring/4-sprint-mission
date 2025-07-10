package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto.*;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContentResponse create(BinaryContentRequest binaryContentRequest);

  BinaryContentResponse find(UUID binaryContentId);
//  BinaryContent find(UUID binaryContentId); // 심화

  List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds);

  void delete(UUID binaryContentId);

  void deleteAll();
}
