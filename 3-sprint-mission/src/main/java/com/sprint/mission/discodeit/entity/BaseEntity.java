package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    public BaseEntity() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        id = UUID.randomUUID();
    }

    public void setUpdatedAt() {
        this.updatedAt = Instant.now();
    }
}
