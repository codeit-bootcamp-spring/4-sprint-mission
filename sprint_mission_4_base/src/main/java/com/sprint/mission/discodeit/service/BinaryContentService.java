package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentCreateRequest request) throws IOException;
    BinaryContentResponseDto find(UUID binaryContentId);
    List<BinaryContentResponseDto> findAllByIdIn(BinaryContentRequest binaryContentIds);
    void delete(UUID binaryContentId);
}
