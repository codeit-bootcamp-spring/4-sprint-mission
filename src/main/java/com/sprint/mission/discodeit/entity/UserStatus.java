package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {
    @Column(nullable = false)
    private Instant lastActiveAt;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public UserStatus(User user, Instant lastActiveAt) {
        super();
        this.user = user;
        this.lastActiveAt = lastActiveAt;
    }

    public UserStatus(User testUser) {
        super();
        this.user = testUser;
    }

    public boolean isRecentlyActive() {
        return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "userId=" + user.getId() +
                ", lastActiveAt=" + lastActiveAt +
                ", online=" + this.isOnline() +
                '}';
    }

    public UUID getUserId() {
        return user.getId();
    }

    public void update(Instant newLastActiveAt) {
        if (lastActiveAt.isAfter(newLastActiveAt)) {
            System.out.println("가지고 있는 데이터가 더 최신입니다.");
            return;
        }
        this.lastActiveAt = newLastActiveAt;
    }

    public boolean isOnline() {
        return isRecentlyActive();
    }
}
