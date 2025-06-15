package com.sprint.mission.discodeit.domain;

public class ServiceType {

    public enum ServiceStatus {
        FILE("파일"),
        JCF("자바 콜렉션 프레임 워크");

        private String description;

        ServiceStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
