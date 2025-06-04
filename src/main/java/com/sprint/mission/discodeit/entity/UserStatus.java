package com.sprint.mission.discodeit.entity;

public enum UserStatus {
    ACTIVE("온라인"),
    INACTIVE("오프라인"),
    AWAY("자리비움"),
    DELETED("삭제된 유저"),
    BANNED("차단된 유저"),;

    private String userStatus;

    UserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

}
