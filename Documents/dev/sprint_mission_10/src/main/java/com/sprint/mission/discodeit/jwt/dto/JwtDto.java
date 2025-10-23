package com.sprint.mission.discodeit.jwt.dto;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtDto {
    private UserDto userDto;
    private String accessToken;
    private String refreshToken;
}
