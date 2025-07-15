package com.codeit.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Setter
@Getter
public class Channel extends BaseEntity implements Serializable {

  private String channelName;
  private UUID hostUserId;
  private ChannelType channelType;
  private String channelDescription = null;

  private final Set<UUID> messageIds = new HashSet<>();
  private final Set<UUID> userIds = new HashSet<>();

  public Channel(UUID hostUserId, String name, String description) {
    super();
    this.hostUserId = hostUserId;
    this.channelName = name;
    this.channelDescription = description;
    this.channelType = ChannelType.PUBLIC; // public 채널 생성
  }

  public Channel(UUID hostUserId) {
    super();
    this.hostUserId = hostUserId;
    this.channelType = ChannelType.PRIVATE; //private 채널 생성
  }

  public Channel(String name, String description) {
    super();
    this.channelName = name;
    this.channelDescription = description;
    this.channelType = ChannelType.PUBLIC; // public 채널 생성
  }

  public Channel() {
    super();
    this.channelType = ChannelType.PRIVATE; //private 채널 생성
  }

  public void addUser(User user) {
    if (!userIds.add(user.getId())) {
      //System.out.println("User: " + user.getId() + ", already exists!");
      return;
    }
    user.addChannel(this);
  }

  public void removeUser(User user) {
    if (!userIds.remove(user.getId())) {
      System.out.println("User: " + user.getId() + ", isn't exists!");
      return;
    }
    user.removeChannel(this);
  }

  public void addMessage(Message message) {
    if (!messageIds.add(message.getId())) {
      System.out.println("Message: " + message.getId() + ", already exists!");
    }
  }

  public void removeMessage(Message message) {
    if (!messageIds.remove(message.getId())) {
      System.out.println("Message: " + message.getId() + ", isn't exists!");
    }
  }

  public void updateChannelName(String channelName) {
    super.updateUpdatedAt();
    this.channelName = channelName;
  }

  public void updateHostUser(User newHostUser) {
    updateUpdatedAt();
    this.hostUserId = newHostUser.getId();
  }

  public void clearUserIds() {
    userIds.clear();
  }

  public void clearMessageIds() {
    messageIds.clear();
  }
}