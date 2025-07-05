package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageState {
    VISIBLE("표시"),
    INVISIBLE("숨김"),
    DELETED("삭제");

    private final String description;
}
