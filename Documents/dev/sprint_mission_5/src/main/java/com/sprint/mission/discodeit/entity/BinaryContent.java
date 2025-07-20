package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Instant createdAt;

    private UUID id;
    private UUID userId;
    private UUID messageId;
    private byte[] data;
    private String fileName;
    private String fileType;
}
