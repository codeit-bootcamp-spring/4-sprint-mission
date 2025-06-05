package com.sprint.mission.discodeit.entity;

import java.util.UUID;

abstract class BaseEntity {
    // ? 지금과 같은 상황에 BaseEntitiy를 만드나요 > yes ?
    private UUID id;
    private long createdAt;
    private long updatedAt;

    // 생성자
    // final 처럼 사용하기 위해 사용
    public BaseEntity(){
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // setter && getter
    // updatedAt 제외하고 나머지는 final이라 setter X
    public void setUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();;
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }
}
