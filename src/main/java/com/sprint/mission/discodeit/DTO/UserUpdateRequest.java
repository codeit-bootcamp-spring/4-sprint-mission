package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        String newUsername,
        String newEmail,
        String newPassword,
        byte[] newProfileImageData
) {}