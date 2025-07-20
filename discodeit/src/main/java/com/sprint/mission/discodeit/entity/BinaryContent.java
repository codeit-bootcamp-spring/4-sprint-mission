package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id; //BinaryContent의 아이디
    private final UUID UserId; //User의 profile 이미지일 경우
    private final UUID messageId; //Message에 첨부된 경우
    private final byte[] size;
    private final String fileName;
    private final String contentType;
    private final Instant createdAt;

    public BinaryContent(UUID ownerUserId, UUID messageId, byte[] size, String fileName, String contentType) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.UserId = ownerUserId;
        this.messageId = messageId;
        this.size = size.clone();
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public byte[] getDatas() {
        return size.clone();
    }
}
