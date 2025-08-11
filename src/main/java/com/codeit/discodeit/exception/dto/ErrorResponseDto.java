package com.codeit.discodeit.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {

  private String description;
  private Object Content;
}