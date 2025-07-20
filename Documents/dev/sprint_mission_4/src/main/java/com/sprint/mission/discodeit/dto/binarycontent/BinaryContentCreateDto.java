package com.sprint.mission.discodeit.dto.binarycontent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class BinaryContentCreateDto {
    private UUID userId;
    private UUID messageId;
    MultipartFile file;
    private String fileType;
}
