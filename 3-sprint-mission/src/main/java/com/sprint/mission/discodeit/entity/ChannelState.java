package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChannelState {
    ACTIVATED("활성화"),
    DEACTIVATED("비활성화"),
    DELETED("삭제");

    private final String description;
}
