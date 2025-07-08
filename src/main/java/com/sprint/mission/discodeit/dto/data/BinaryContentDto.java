package com.sprint.mission.discodeit.dto.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class BinaryContentDto {
    private UUID userId;
    private UUID messageId;
    private byte[] bytes;
    private String fileName;
    private String fileType;
}
