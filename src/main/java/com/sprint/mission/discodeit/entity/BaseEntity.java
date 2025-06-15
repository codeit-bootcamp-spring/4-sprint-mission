package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class BaseEntity implements Serializable {

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    public BaseEntity() {

        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;
    }

    public UUID getId() {
        return id;
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

    public String getFormattedCreatedAt() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(createdAt);
    }
    public String getFormattedUpdatedAt() {
        if(updatedAt != null) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(updatedAt);
        } else {
            return "수정된 시간이 존재하지 않습니다.";
        }
    }
}
