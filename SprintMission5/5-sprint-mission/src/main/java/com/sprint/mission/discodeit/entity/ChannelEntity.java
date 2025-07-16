package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ChannelEntity extends BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  private List<UUID> userIds = new ArrayList<>();
  private List<UUID> messageIds = new ArrayList<>();
  @Setter
  private String channelName;
  private ChannelState state = ChannelState.ACTIVATED;
  @Setter
  private ChannelType type;
  @Setter
  private String description;

  public ChannelEntity(String channelName, String description, ChannelType type) {
    this.channelName = channelName;
    this.description = description;
    this.type = type;
  }

  public void addUser(UserEntity userEntity) {
    if (userIds.contains(userEntity.getId())) {
      return;
    }
    userIds.add(userEntity.getId());
    userEntity.getChannelIds().add(this.getId());
  }

  public void removeUser(UserEntity userEntity) {
    if (!userIds.contains(userEntity.getId())) {
      return;
    }
    userIds.remove(userEntity.getId());
    userEntity.getChannelIds().remove(this.getId());
  }

  public void addMessage(MessageEntity messageEntity) {
    if (messageIds.contains(messageEntity.getId())) {
      return;
    }
    messageIds.add(messageEntity.getId());
    messageEntity.addChannel(this);
  }

  public void removeMessage(MessageEntity messageEntity) {
    if (!messageIds.contains(messageEntity.getId())) {
      return;
    }
    messageIds.remove(messageEntity.getId());
    messageEntity.removeChannel(this);
  }

  // 상태
  public void setToActivate() {
    state = ChannelState.ACTIVATED;
  }

  public void setToDeactivate() {
    state = ChannelState.DEACTIVATED;
  }

  public void setToDelete() {
    state = ChannelState.DELETED;
  }

  @Override
  public String toString() {
    String userIdString = userIds.stream().map(UUID::toString).collect(Collectors.joining(", "));

    return "Channel{"
        + "\n"
        + "channelName="
        + channelName
        + "\n"
        + "channelsUsers="
        + userIdString
        + "\n"
        + "createdAt="
        + getCreatedAt()
        + "\n"
        + "updatedAt="
        + getUpdatedAt()
        + "\n"
        + '}'
        + "\n";
  }
}
