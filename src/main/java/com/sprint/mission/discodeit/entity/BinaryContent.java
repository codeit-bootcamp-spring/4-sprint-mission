package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private ActiveStatus active;

    private String contentType;
    private final byte[] bytes;
    private Long size;
    private String fileName;

    public BinaryContent(byte[] bytes, String contentType, Long size, String fileName) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.active = ActiveStatus.ACTIVE;
        this.bytes = bytes;
        this.contentType = contentType;
        this.size = size;
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", active=" + active +
                ", contentType=" + contentType +
                ", newContent=" + Arrays.toString(bytes) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BinaryContent that = (BinaryContent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
