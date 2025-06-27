package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class BinaryContentDto {
    private UUID userId;
    private UUID messageId;
    private byte[] data;
    private String fileName;
    private String fileType;
}
