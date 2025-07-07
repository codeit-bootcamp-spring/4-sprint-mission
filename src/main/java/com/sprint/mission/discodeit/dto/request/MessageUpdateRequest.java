package com.sprint.mission.discodeit.dto.request;

import java.util.List;

public record MessageUpdateRequest(
        String newContent,
        List<BinaryContentCreateRequest> newAttachments
) {
}
