package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

    public UserStatus(User user) {
        this.user = user;
        this.lastActiveAt = Instant.now();
    }

    public void updateAccessTime() {
        this.lastActiveAt = Instant.now();
        setUpdatedAt();
    }

    public void updateAccessTime(Instant time) {
        this.lastActiveAt = time;
        setUpdatedAt();
    }

    public boolean isOnline() {
        Instant threshold = Instant.now().minus(ONLINE_THRESHOLD);
        return lastActiveAt.isAfter(threshold);
    }
}
