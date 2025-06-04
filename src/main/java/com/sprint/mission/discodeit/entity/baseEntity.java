package com.sprint.mission.discodeit.entity;

public abstract class baseEntity {
    protected Long createdAt;
    protected Long updatedAt;

    public void newUpdatedAt()
    {
        this.updatedAt = System.currentTimeMillis();
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }
}
