package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BaseEntity {
    protected UUID id;
    protected final long createdAt;
    protected long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = 0;
    }

    public UUID getId() {
        return id;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
