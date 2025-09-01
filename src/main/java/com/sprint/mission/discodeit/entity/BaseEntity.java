package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BaseEntity {

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        long time = System.currentTimeMillis();
        this.createdAt = time;
        this.updatedAt = time;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updatedAt;
    }

    public void updateTimeStamp(){
        this.updatedAt = System.currentTimeMillis();
    }
}
