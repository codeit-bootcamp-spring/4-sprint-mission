package com.sprint.mission.discodeit.entity;

public enum UserState {
  ONLINE("온라인"),
  OFFLINE("오프라인"),
  DELETED("탈퇴");

  private String description;

  private UserState(String description) {
    this.description = description;
  }
}
