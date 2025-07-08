package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateUserDto {
    private String nickName;
    private String email;
    private BinaryContent image;

    public UpdateUserDto(String newNickName, String newEmail) {
        this.nickName = newNickName;
        this.email = newEmail;
    }
}
