package com.sprint.mission.discodeit.entity.DTO;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateUserDTO {
    private String nickName;
    private String email;
    private BinaryContent image;

    public UpdateUserDTO(String newNickName, String newEmail) {
        this.nickName = newNickName;
        this.email = newEmail;
    }
}
