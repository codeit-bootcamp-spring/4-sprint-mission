package com.codeit.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BaseEntity implements Serializable {

    private final Instant createdAt = Instant.now();
    private Instant updatedAt = createdAt;
    private UUID id = UUID.randomUUID();

    public void updateUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    public boolean equalsId(BaseEntity baseEntity) {
        return this.id.equals(baseEntity.getId());
    }

    public boolean equalsId(UUID baseEntityId) {
        return this.id.equals(baseEntityId);
    }

}