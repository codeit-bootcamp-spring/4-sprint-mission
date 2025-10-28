package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record S3UploadEvent(UUID binaryContentId, byte[] bytes) {}
