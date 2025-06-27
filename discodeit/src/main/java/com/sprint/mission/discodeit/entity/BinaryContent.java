package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID UserId; //User의 profile 이미지일 경우
    private final UUID messageId; //Message에 첨부된 경우
    private final byte[] datas;
    private final String filename;
    private final String fileType;
    private final Instant createdAt;

    public BinaryContent(UUID ownerUserId, UUID messageId, byte[] datas, String filename, String fileType) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.UserId = ownerUserId;
        this.messageId = messageId;
        this.datas = datas.clone();
        this.filename = filename;
        this.fileType = fileType;
    }

    public byte[] getDatas() {
        return datas.clone();
    }
}
