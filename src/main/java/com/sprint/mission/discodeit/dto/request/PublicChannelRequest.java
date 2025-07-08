package com.sprint.mission.discodeit.dto.request;

public class PublicChannelRequest {
    public static class Create {
        String channelName;
        String channelType;
    }

    public static class Update {
        String newChannelName;
        String newChannelType;
    }
}
