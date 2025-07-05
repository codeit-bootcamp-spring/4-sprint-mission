package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentDto.create request);

    BinaryContent find(UUID binaryContentId);

    List<BinaryContent> findAllByIdIn(BinaryContentDto.getBinaryContents request);

    void delete(UUID binaryContentId);
}
