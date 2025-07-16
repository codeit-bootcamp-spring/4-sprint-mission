package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record UserUpdateServiceRequest(
    UUID id,
    String newUsername,
    String newEmail,
    String newPassword,
    MultipartFile profile
) {

}