package com.sprint.mission.discodeit.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class BinaryContentDTO {
    private UUID userId;
    private UUID messageId;
    private byte[] bytes;
    private String fileName;
    private String fileType;
}
