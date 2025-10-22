package com.sprint.mission.discodeit.jwt.dto;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtDto {
  public UserDto userDto;
  public String accessToken;

}
