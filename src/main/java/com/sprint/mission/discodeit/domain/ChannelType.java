package com.sprint.mission.discodeit.domain;

public class ChannelType {

    public enum ChannelStatus {
        ACTIVE("활성"),
        INACTIVE("비활성");

        private String description;

        ChannelStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
