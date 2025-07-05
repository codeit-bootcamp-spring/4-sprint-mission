package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record MessageMultipartUpdateDto(
        UUID id, String content, List<UUID> removeAttachments, List<MultipartFile> newAttachments) {}
