package com.sprint.mission.discodeit.entity;

public abstract class BaseEntity {
    protected final Long createdAt;
    protected Long updatedAt;

    public BaseEntity() {
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
