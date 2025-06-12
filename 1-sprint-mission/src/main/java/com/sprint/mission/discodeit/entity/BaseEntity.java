package com.sprint.mission.discodeit.entity;

public abstract class BaseEntity {
    protected final Long createdAt;
    protected Long updatedAt;

    public BaseEntity() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public BaseEntity(Long createdAt, Long updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
