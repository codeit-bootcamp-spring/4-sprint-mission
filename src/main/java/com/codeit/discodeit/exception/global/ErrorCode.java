package com.codeit.discodeit.exception.global;

public interface ErrorCode {
  int getStatus();
  String getMessage();
  String getName();
}