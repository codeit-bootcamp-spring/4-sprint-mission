package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.DTO.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public interface BinaryContentService {

    BinaryContentResponse create(BinaryContentCreateRequest requset);
    BinaryContentResponse find(UUID id);
    void delete(UUID id);

}
