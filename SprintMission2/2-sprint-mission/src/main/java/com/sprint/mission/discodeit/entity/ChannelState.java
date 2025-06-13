package com.sprint.mission.discodeit.entity;

public enum ChannelState {
  ACTIVATED("활성화"),
  DEACTIVATED("비활성화"),
  DELETED("삭제");

  private String description;

  private ChannelState(String description) {
    this.description = description;
  }
}
