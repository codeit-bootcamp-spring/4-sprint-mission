package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentSaveFailException;

public interface BinaryContentStorageService {
    void save(BinaryContentCreatedEvent event);

    void recover(BinaryContentSaveFailException e, BinaryContentCreatedEvent event);
}
