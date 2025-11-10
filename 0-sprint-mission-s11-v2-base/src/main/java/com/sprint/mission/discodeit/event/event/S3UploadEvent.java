package com.sprint.mission.discodeit.event.event;

import java.util.UUID;

public record S3UploadEvent(UUID receiverId, UUID binaryContentId, byte[] bytes) {}
