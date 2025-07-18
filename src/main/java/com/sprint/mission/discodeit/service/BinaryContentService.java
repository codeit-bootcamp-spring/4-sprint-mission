package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContent;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    com.sprint.mission.discodeit.entity.BinaryContent create(BinaryContentCreateRequest request);
    com.sprint.mission.discodeit.entity.BinaryContent find(UUID binaryContentId);
    List<com.sprint.mission.discodeit.entity.BinaryContent> findAllByIdIn(List<UUID> binaryContentIds);
    void delete(UUID binaryContentId);
    BinaryContent makeDto(com.sprint.mission.discodeit.entity.BinaryContent binaryContent);
}
