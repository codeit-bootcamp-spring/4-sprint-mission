package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class BinaryContent extends ImmutableBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private final BinaryContentType binaryContentType;  // PROFILE(USER), ATTACHMENT(MESSAGE)
    private final byte[] bytes;
    private final String fileName;
    private final FileType fileType;
    private final Long fileSize;

    //
    @Setter
    private UUID userId;
    @Setter
    private UUID messageId;


    public BinaryContent(UUID userId, UUID messageId, BinaryContentType binaryContentType, byte[] bytes, String fileName, FileType fileType, Long fileSize) {
        super();
        this.userId = userId;
        this.messageId = messageId;
        this.binaryContentType = binaryContentType;
        this.bytes = bytes;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
}
