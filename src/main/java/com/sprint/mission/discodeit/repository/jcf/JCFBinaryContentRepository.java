package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFBinaryContentRepository implements BinaryContentRepository {
    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        return null;
    }

    @Override
    public List<UUID> saveAll(List<BinaryContent> binaryContents) {
        return List.of();
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<BinaryContent> findAll() {
        return List.of();
    }

    @Override
    public List<BinaryContent> findAllById(List<UUID> binaryIds) {
        return List.of();
    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void deleteAllByBinaryIds(List<UUID> binaryIds) {

    }
}
