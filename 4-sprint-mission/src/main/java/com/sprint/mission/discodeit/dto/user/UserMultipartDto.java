package com.sprint.mission.discodeit.dto.user;

import org.springframework.web.multipart.MultipartFile;

public record UserMultipartDto(
        String userName, String userEmail, String password, boolean status, MultipartFile image) {}
