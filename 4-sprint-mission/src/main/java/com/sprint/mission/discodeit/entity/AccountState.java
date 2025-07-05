package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AccountState {
    ACTIVE("정상"),
    DELETED("탈퇴");

    private final String description;
}
