package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "read_statuses", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel_id"}))
@NoArgsConstructor
@Getter
@Setter
public class ReadStatus extends BaseUpdatableEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false, unique = true)
    private Channel channel;

    @Column(nullable = false)
    private Instant lastReadAt;

}
