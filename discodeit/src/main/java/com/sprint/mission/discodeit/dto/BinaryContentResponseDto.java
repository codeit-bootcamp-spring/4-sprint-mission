package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class BinaryContentResponseDto {
    private UUID id;
    private UUID UserId;
    private UUID messageId;
    private byte[] data;
    private String filename;
    private String fileType;
}
