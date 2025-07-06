package com.sprint.mission.discodeit.dto.form;

import org.springframework.web.multipart.MultipartFile;

public record UserForm(
        String username,
        String email,
        String password,
        MultipartFile profile
) {
}
