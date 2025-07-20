package com.sprint.mission.discodeit.dto.binarycontent;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class BinaryContentCreateDto {
    private UUID userId;
    private UUID messageId;
    private byte[] bytes;
    private String fileName;
    private String fileType;
}
