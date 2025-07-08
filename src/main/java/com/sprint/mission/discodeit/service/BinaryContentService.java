package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService { // BinaryContentService CRUD
    // BinaryContent가 뭔지 아직도 머리에 잘 들어오지 않는다..
    BinaryContentDto createBinaryContent(BinaryContentRequest binaryContentRequest);
    BinaryContentDto searchBinaryContent(UUID binaryId, BinaryContentRequest binaryContentRequest);
    List<BinaryContentDto> searchAllBinaryContent();
    BinaryContentDto updateBinaryContent(UUID binaryId, BinaryContentRequest binaryContentRequest);
    void deleteBinaryContent(UUID binaryId);
}
