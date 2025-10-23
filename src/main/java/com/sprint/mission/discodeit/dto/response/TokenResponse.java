package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {

  private UserDto userDto;
  private String accessToken;   // 새로 발급된 Access Token
  private String refreshToken;  // 갱신된 Refresh Token (선택적으로 회전 시 반환)

}
