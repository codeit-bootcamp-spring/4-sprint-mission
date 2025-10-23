package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

@Getter
public class TokenReissueRequest {

  /**
   * 만료된 Access Token (선택적)
   */
  private String accessToken;

  /**
   * Refresh Token (필수)
   */
  private String refreshToken;
}
