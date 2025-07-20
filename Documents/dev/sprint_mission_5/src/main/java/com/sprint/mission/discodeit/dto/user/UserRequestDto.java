package com.sprint.mission.discodeit.dto.user;


import java.util.UUID;

public record UserRequestDto (
    String username,
    String email,
    String password,
    UUID profileImageId
    ) {
    public static UserRequestDto create(String username, String email, String password, UUID profileImageId) {
        return new UserRequestDto(username, email, password, profileImageId);
    }
}

