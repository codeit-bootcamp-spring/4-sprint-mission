package com.sprint.mission.discodeit.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BinaryContentCreatedEvent {

    private final UUID binaryContentId;

    private final byte[] bytes;

    private final String filename;

    private final String contentType;

    public BinaryContentCreatedEvent(UUID binaryContentId, byte[] bytes,
                                     String filename, String contentType) {
        this.binaryContentId = binaryContentId;
        this.bytes = bytes;
        this.filename = filename;
        this.contentType = contentType;
    }

    public byte[] getBytes() {
        return (bytes == null) ? null : bytes.clone();
    }
}
