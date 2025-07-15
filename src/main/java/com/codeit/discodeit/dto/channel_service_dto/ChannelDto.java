package com.codeit.discodeit.dto.channel_service_dto;

import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ChannelType;
import com.codeit.discodeit.entity.Message;
import java.time.Instant;
import java.util.ArrayList;
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
  private ArrayList<UUID> participantIds;
  private Instant lastMessageAt = null;

  public ChannelDto(Channel channel, Message lastMessage) {
    this.id = channel.getId();
    this.name = channel.getChannelName();
    this.type = channel.getChannelType();
    this.description = channel.getChannelDescription();
    this.participantIds = new ArrayList<>(channel.getUserIds());
    if (lastMessage != null) {
      this.lastMessageAt = lastMessage.getCreatedAt();
    }
  }
}
