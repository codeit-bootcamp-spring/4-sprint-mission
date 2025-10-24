package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(MultipartFile file);

    BinaryContent find(UUID id);

    List<BinaryContent> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);

    BinaryContent updateStatus(UUID binaryContentId, BinaryContentStatus status);
}
