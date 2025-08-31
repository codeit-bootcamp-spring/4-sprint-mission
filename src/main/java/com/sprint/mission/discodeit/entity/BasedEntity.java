package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BasedEntity {
    private final UUID id;  // 랜덤한 UUID 값으로 중복 방지
    private final Long createdAt;  // 생성 시간
    private Long updatedAt;  // 최종 업데이트 시간
    private boolean isActive;  // 객체가 존재하는가 ( 컨트롤 하기 적합한 가 )

    public BasedEntity() {
        Long time = System.currentTimeMillis();
        this.id = UUID.randomUUID();
        this.createdAt = time;
        this.updatedAt = time;
    }

    /**
     * Getter
     **/
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * Setter
     **/
    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
