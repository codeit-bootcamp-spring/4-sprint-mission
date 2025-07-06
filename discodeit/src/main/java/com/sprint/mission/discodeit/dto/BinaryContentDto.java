package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class BinaryContentDto {

    @AllArgsConstructor
    @Getter
    public static class BinaryContentCreateDto {
        private UUID contentId;
        private UUID userId;
        private UUID messageId;
        private MultipartFile file;
    }

    @AllArgsConstructor
    @Getter
    public static class BinaryContentResponseDto {
        private UUID id;
        private UUID userId;
        private UUID messageId;
        private byte[] data;
        private String fileName;
        private String fileType;
    }
}
