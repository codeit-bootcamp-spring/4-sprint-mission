package com.sprint.mission.discodeit.entity;

public enum MessageState {
    VISIBLE(""),
    INVISIBLE("숨김"),
    DELETED("삭제");

    private String state;

    private MessageState(String state) {
        this.state = state;
    }
}
