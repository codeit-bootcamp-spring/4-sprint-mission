package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable {
    protected final Long createdAt;
    protected Long updatedAt;
    private static final long serialVersionUID = 1L;

    public BaseEntity() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
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
