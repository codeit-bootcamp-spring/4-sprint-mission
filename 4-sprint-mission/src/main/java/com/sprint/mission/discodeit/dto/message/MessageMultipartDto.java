package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record MessageMultipartDto(
        String content, List<MultipartFile> binaryContents, UUID userId, UUID channelId) {}
