package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateServiceRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentService {

    void createBinaryContent(BinaryContentCreateServiceRequest request);

    BinaryContentResponse findById(UUID binaryContentId);

    List<BinaryContentResponse> findAllByIdIn(List<UUID> ids);

    void deleteById(UUID binaryContentId);

    ResponseEntity<?> download(BinaryContentResponse response);
}
