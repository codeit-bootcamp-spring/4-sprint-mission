package com.sprint.mission.discodeit.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateUserDTO {
    private String nickName;
    private String email;
    private BinaryContentDTO binaryContent;

    public CreateUserDTO(String nickName, String email) {
        this.nickName = nickName;
        this.email = email;
    }

}
