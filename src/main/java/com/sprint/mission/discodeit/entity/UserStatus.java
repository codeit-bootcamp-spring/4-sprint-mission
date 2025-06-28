package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID userId;
    @Setter
    private Instant lastLoginTimes;
    private UserStatusType userStatusType;


    public UserStatus(UUID userId, Instant lastLoginTimes) {
        super();
        this.userId = userId;
        this.lastLoginTimes = lastLoginTimes;
        this.userStatusType = checkUserOnline();
    }

    public UserStatusType checkUserOnline() {

        Instant fiveMinutesBeforeCurrentTime = Instant.ofEpochSecond(Instant.now().getEpochSecond()).minusSeconds(5 * 60);
        if (lastLoginTimes.isAfter(fiveMinutesBeforeCurrentTime)) {
            return UserStatusType.ONLINE;
        }
        return UserStatusType.OFFLINE;
    }

    public void update(Instant newLastLoginTimes) {
        boolean anyValueUpdated = false;

        if (newLastLoginTimes != null && !newLastLoginTimes.equals(lastLoginTimes)) {
            this.lastLoginTimes = newLastLoginTimes;
            checkUserOnline();
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.ofEpochSecond(Instant.now().getEpochSecond()));
        }
    }
}
