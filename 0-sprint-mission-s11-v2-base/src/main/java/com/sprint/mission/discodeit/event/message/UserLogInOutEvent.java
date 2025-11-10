package com.sprint.mission.discodeit.event.message;

import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserLogInOutEvent {
  private final UUID userId;
  private final boolean isLoggedIn;



}
