package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class BasedEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;  // 랜덤한 UUID 값으로 중복 방지
    private final Instant createdAt;  // 생성 시간
    private Instant updatedAt;  // 최종 업데이트 시간
    private ActiveStatus active;  // 객체가 존재하는가 ( 컨트롤 하기 적합한 가 )

    public BasedEntity() {
        Instant time = Instant.now();
        this.id = UUID.randomUUID();
        this.createdAt = time;
        this.updatedAt = time;
        this.active = ActiveStatus.INACTIVE;
    }

    /**
     * Setter
     **/
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setActive(ActiveStatus active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BasedEntity that = (BasedEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
