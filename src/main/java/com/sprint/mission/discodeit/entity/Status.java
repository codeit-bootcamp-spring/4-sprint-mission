package com.sprint.mission.discodeit.entity;

public enum Status {
    ONLINE("온라인"),
    OFFLINE("오프라인"),
    AWAY("자리비움"),
    BUSY("다른 용무 중");

    private String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
