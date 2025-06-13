package com.sprint.mission.discodeit.entity;

public enum MessageState {
  VISIBLE("표시"),
  INVISIBLE("숨김"),
  DELETED("삭제");

  private String description;

  private MessageState(String description) {
    this.description = description;
  }
}
