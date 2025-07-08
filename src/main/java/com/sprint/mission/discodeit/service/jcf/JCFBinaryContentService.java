package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class JCFBinaryContentService implements BinaryContentService {

    @Override
    public BinaryContentDto createBinaryContent(BinaryContentRequest binaryContentRequest) {
        return null;
    }

    @Override
    public BinaryContentDto searchBinaryContent(UUID binaryId, BinaryContentRequest binaryContentRequest) {
        return null;
    }

    @Override
    public List<BinaryContentDto> searchAllBinaryContent() {
        return List.of();
    }

    @Override
    public BinaryContentDto updateBinaryContent(UUID binaryId, BinaryContentRequest binaryContentRequest) {
        return null;
    }

    @Override
    public void deleteBinaryContent(UUID binaryId) {

    }
}
