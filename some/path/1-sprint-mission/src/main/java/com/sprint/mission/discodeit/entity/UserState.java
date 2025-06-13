package com.sprint.mission.discodeit.entity;

public enum UserState {
    ONLINE("온라인"),
    OFFLINE("오프라인"),
    DELETED("탈퇴");

    private String state;

    private UserState(String state) {
        this.state = state;
    }
    public String getState() {
        return state;
    }
}