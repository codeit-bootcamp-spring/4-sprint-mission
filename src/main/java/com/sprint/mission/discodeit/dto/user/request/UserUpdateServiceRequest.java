package com.sprint.mission.discodeit.dto.user.request;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class UserUpdateServiceRequest {

    private final UUID userId;

    private final String newUsername;
    private final String newEmail;
    private final String newPassword;
    private final MultipartFile profile;
}
