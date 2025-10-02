package com.sprint.mission.discodeit.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BinaryContentErrorCode implements ErrorCode {


    BINARY_CONTENT_NOT_FOUND(404, "BINARY_CONTENT_001", "binary content 없음"),
    MULTIPART_FILE_CONVERT_FAILED(500, "BINARY_CONTENT_002", "MULTIPART_FILE_CONVERT_FAILED");

    private final int status;
    private final String code;
    private final String message;
}
