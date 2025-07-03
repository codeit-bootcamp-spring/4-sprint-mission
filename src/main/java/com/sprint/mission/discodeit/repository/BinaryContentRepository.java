package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    List<BinaryContent> loadBinaryContents();
    void saveBinaryContents(List<BinaryContent> contentsList);

    void createBinaryContent(BinaryContent contents);
    Optional<BinaryContent> findBinaryContentByBinaryContentId(UUID binaryContentsId);

    void deleteBinaryContentByBinaryContentId(UUID binaryContentId);
    List<BinaryContent> findBinaryContentListByReferenceId(UUID referenceId);
}
