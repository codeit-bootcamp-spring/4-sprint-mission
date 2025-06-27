package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContents;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentsRepository {

    List<BinaryContents> loadBinaryContents();
    void saveBinaryContents(List<BinaryContents> contentsList);

    void createBinaryContents(BinaryContents contents);
    Optional<BinaryContents> findBinaryContentsByBinaryContentsId(UUID binaryContentsId);

    void deleteBinaryContensByBinaryContentsId(UUID binaryContentId);
    List<BinaryContents> findBinaryContentsByReferenceId(UUID referenceId);
}
