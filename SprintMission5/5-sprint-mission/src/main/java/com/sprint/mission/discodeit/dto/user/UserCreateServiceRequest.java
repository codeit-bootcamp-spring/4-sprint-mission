package com.sprint.mission.discodeit.dto.user;

import org.springframework.web.multipart.MultipartFile;

public record UserCreateServiceRequest(
    String username,
    String email,
    String password,
    MultipartFile profile
) {

}