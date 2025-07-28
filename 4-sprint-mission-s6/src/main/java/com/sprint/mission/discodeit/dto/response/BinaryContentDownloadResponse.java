package com.sprint.mission.discodeit.dto.response;

public record BinaryContentDownloadResponse(
        String fileName,
        long size,
        String contentType,
        byte[] data
) {

}
