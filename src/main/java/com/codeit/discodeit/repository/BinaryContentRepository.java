package com.codeit.discodeit.repository;

import com.codeit.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    void createBinaryContent(BinaryContent contents);
    void deleteBinaryContent(BinaryContent binaryContent);

    Optional<BinaryContent> findBinaryContentByBinaryContentId(UUID binaryContentsId);
    List<BinaryContent> findBinaryContentListByBinaryContentIds(List<UUID> binaryContentIds);
}