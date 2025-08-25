package com.codeit.discodeit.exception.channel;

import com.codeit.discodeit.exception.global.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChannelErrorCode implements ErrorCode {

  CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "채널을 찾을 수 없습니다."),
  CHANNEL_NAME_DUPLICATE(HttpStatus.BAD_REQUEST.value(), "채널을 이름 중복 입니다."),
  No_Participants_Channel(HttpStatus.BAD_REQUEST.value(), "참여 인원잉 없습니다.."),;

  private final int status;
  private final String message;

  ChannelErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

  @Override
  public String getName() {
    return name();
  }
}
