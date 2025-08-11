package com.codeit.discodeit.dto.channel_service_dto;

import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChannelDto {

  private UUID id;
  private ChannelType type;
  private String name = null;
  private String description = null;
  private List<UserDto> participants;
  private Instant lastMessageAt = null;
}

