package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    public BinaryContent save(BinaryContent binaryContent);

    public Optional<BinaryContent> findById(UUID id);

    public List<BinaryContent> findAll();

    public void delete(UUID id);
}
