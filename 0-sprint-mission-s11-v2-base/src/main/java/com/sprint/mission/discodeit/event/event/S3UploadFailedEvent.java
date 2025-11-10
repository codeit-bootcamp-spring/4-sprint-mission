package com.sprint.mission.discodeit.event.event;

import java.util.UUID;

public record S3UploadFailedEvent(
  UUID adminId,
  String title,
  String content
) {}