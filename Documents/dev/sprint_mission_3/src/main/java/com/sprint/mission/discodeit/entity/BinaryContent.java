package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Instant createdAt;

    private UUID id;
    private UUID userId;
    private UUID messageId;
    private byte[] data;
    private String fileName;
    private String fileType;

    public BinaryContent(UUID id,UUID userId, UUID messageId, byte[] data, String fileName, String fileType ) {
        this.id = id;
        this.userId = userId;
        this.messageId = messageId;
        this.data = data;
        this.fileName = fileName;
        this.fileType = fileType;

        this.createdAt = Instant.now();
    }
}
