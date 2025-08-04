package com.sprint.mission.discodeit.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record
MutilFileCreateRequest(
        List<String> fileName,
        List<String> contentType,
        List<MultipartFile> bytes
) {
}
