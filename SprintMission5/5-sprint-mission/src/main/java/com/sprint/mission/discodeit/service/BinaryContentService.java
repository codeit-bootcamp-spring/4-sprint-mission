package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContent;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  public BinaryContent createBinaryContent(
      BinaryContentCreateServiceRequest binaryContentCreateServiceRequest);

  public BinaryContent findBinaryContentById(UUID id);

  public List<BinaryContent> findAllBinaryContentById(UUID id);

  public List<BinaryContent> findAllBinaryContentByIdIn(List<UUID> ids);

  public void deleteBinaryContent(UUID id);
}
