package com.sprint.mission.discodeit.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChannelErrorCode implements ErrorCode {


    CHANNEL_NOT_FOUND(404, "CHANNEL_001", "CHANNEL_NOT_FOUND"),
    PRIVATE_CHANNEL_NOT_EDITABLE(403, "CHANNEL_002", "PRIVATECHANNEL_NOT_EDITABLE"),
    PRIVATE_CHANNEL_DOES_NOT_HAVE_NAME(404, "CHANNEL_003", "PRIVATE_CHANNEL_DOES_NOT_HAVE_NAME"),
    PRIVATE_CHANNEL_DOES_NOT_HAVE_DESCRIPTION(404,"CHANNEL_004","PRIVATE_CHANNEL_DOES_NOT_HAVE_DESCRIPTION"),
    PRIVATE_CHANNEL_DOES_NOT_HAVE_NAME_AND_DESCRIPTION(404, "CHANNEL_005","PRIVATE_CHANNEL_DOES_NOT_HAVE_NAME_AND_DESCRIPTION");

    private final int status;
    private final String code;
    private final String message;
}

