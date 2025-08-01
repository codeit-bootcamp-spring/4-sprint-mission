package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record BinaryContentCreateServiceRequest(UUID userId, UUID messageId, String fileName, String contentType, MultipartFile file) {}
