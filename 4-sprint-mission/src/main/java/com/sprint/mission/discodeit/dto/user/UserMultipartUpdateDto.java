package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record UserMultipartUpdateDto(UUID id, String userName, MultipartFile image) {}
