package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "sse_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SseMessage extends BaseUpdatableEntity {
    // id, createdAt, updatedAt은 이미 있음
    private String topic;
    private String type;
    private String priority;
    private Set<UUID> recipientId;
    private String message;
    private Instant timestamp;
    private Map<String, Object> payload;
}
