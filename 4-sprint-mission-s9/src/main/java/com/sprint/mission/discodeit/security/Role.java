package com.sprint.mission.discodeit.security;

public enum Role {
    ADMIN,
    CHANNEL_MANAGER,
    USER;

    public String authority() { return "ROLE_" + name(); }
}
