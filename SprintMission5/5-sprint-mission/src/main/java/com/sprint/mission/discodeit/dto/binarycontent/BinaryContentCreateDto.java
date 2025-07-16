package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record BinaryContentCreateDto(
    UUID userId,
    UUID messageId,
    String fileName,
    long size,
    String dataType,
    MultipartFile file
) {

}
