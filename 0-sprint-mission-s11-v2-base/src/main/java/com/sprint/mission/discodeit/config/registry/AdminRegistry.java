package com.sprint.mission.discodeit.config.registry;

import java.util.UUID;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Component
public class AdminRegistry {
  private UUID adminId;

  public UUID getAdminId() {
    if (adminId == null)
      throw new IllegalStateException("관리자 계정이 아직 초기화되지 않았습니다.");
    return adminId;
  }
}
