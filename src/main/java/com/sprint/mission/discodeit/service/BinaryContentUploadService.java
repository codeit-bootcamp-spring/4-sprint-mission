package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface BinaryContentUploadService {
    void upload(UUID binaryContentId, byte[] bytes);

    void recover(RuntimeException e, UUID binaryContentId, byte[] bytes);
}
