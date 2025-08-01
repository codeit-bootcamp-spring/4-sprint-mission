package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    public BinaryContentDto createBinaryContent(
            BinaryContentCreateServiceRequest binaryContentCreateServiceRequest);

    public BinaryContentDto findBinaryContentById(UUID id);

    public List<BinaryContentDto> findAllBinaryContentById(UUID id);

    public void deleteBinaryContent(UUID id);
}
