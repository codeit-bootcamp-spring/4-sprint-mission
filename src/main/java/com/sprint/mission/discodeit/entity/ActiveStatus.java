package com.sprint.mission.discodeit.entity;

public enum ActiveStatus {
    ACTIVE("활성화"),  // 활동가능한 정상 상태
    INACTIVE("비활성화"),  // 휴먼
    DELETE("삭제됨");  // 삭제됨

    private String value;

    ActiveStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }}
