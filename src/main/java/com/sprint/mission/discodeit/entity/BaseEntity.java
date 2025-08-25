package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BaseEntity {

    private final long createdAt;
    private long updatedAt;
    private UUID id;

    public long getCreatedAt() {
        return createdAt;
    }
    public long getUpdatedAt() {
        return updatedAt;
    }

    public void updateUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }
    public UUID getId() {
        return id;
    }

    // 생성자 만들기
    public BaseEntity() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.id = UUID.randomUUID();
    }
}
