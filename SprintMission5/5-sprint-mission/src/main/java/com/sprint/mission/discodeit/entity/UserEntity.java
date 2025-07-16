package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserEntity extends BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  @Setter
  private UUID profileId;
  private List<UUID> channelIds = new ArrayList<>();
  private List<UUID> messageIds = new ArrayList<>();
  @Setter
  private String username;
  private AccountState state;
  @Setter
  private String email;
  @Setter
  private String password;

  public UserEntity(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
    state = AccountState.ACTIVE;
    profileId = null;
  }

  @Override
  public String toString() {
    String channelIdString =
        channelIds.stream().map(UUID::toString).collect(Collectors.joining(", "));

    return "User{"
        + "\n"
        + "username=" // userName -> username
        + username
        + "\n"
        + "userState="
        + state
        + "\n"
        + "channelIds="
        + channelIdString
        + "\n"
        + "createdAt="
        + getCreatedAt()
        + "\n"
        + "updatedAt="
        + getUpdatedAt()
        + "\n"
        + '}';
  }

  public void addChannel(ChannelEntity channelEntity) {
    if (channelIds.contains(channelEntity.getId())) {
      return;
    }
    channelIds.add(channelEntity.getId());
    channelEntity.getUserIds().add(this.getId());
  }

  public void removeChannel(ChannelEntity channelEntity) {
    if (!channelIds.contains(channelEntity.getId())) {
      return;
    }
    channelIds.remove(channelEntity.getId());
    channelEntity.getUserIds().remove(this.getId());
  }

  public void addMessage(MessageEntity messageEntity) {
    if (messageIds.contains(messageEntity.getId())) {
      return;
    }
    messageIds.add(messageEntity.getId());
    messageEntity.addUser(this);
  }

  public void removeMessage(MessageEntity messageEntity) {
    if (!messageIds.contains(messageEntity.getId())) {
      return;
    }
    messageIds.remove(messageEntity.getId());
  }

  public void setToOnline() {
    state = AccountState.ACTIVE;
  }

  public void setToDeleted() {
    state = AccountState.DELETED;
  }

  public void removeAllMessages() {
    messageIds.clear();
  }

  public boolean isOnline() {
    return this.state == AccountState.ACTIVE;
  }

}