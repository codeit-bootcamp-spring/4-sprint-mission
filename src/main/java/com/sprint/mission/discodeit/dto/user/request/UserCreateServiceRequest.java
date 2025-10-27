package com.sprint.mission.discodeit.dto.user.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class UserCreateServiceRequest {

    private final String username;
    private final String email;
    private final String password;
    private final MultipartFile profile;
}
