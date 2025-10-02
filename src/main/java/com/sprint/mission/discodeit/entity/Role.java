package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    USER("일반 사용자"),
    CHANNEL_MANAGER("채널 매니저"),
    ADMIN("관리자");

    private final String description;

    private Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static Role from(String value) {
        return Role.valueOf(value.toUpperCase());
    }
}
