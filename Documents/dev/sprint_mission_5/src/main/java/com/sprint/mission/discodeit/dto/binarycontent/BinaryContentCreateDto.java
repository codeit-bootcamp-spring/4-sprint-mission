package com.sprint.mission.discodeit.dto.binarycontent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class BinaryContentCreateDto {
    private UUID userId;
    private UUID messageId;
    private MultipartFile file;
    private String fileType;
    private Instant createdAt;
}
