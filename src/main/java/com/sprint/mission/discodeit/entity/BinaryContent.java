package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

    private final UUID id;
    private final Instant createdAt;

    private  final UUID userId;
    private  final UUID messageId;

    private final byte[] bytes;
    private final String fileName;
    private final BinaryContentType fileType;

    public BinaryContent(UUID userId, UUID messageId, byte[] bytes, String fileName, BinaryContentType fileType) {
        this.userId = userId;
        this.messageId = messageId;

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.bytes = bytes;
        this.fileName = fileName;
        this.fileType = fileType;
    }
}
