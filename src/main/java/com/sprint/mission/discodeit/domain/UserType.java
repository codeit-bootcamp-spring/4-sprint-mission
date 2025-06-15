package com.sprint.mission.discodeit.domain;

public class UserType {

    public enum UserStatus {
        ACTIVE("온라인"),
        SLEEP("휴면"),
        BANNED("정지"),
        QUIT("탈퇴");

        private String description;

        UserStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
