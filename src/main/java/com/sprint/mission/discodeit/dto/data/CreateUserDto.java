package com.sprint.mission.discodeit.dto.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateUserDto {
    private String nickName;
    private String email;
    private BinaryContentDto binaryContent;

    public CreateUserDto(String nickName, String email) {
        this.nickName = nickName;
        this.email = email;
    }
}
