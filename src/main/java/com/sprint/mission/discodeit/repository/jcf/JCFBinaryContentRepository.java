package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {

  private final Map<UUID, BinaryContent> data; // BinaryContentId : BinaryContent

  public JCFBinaryContentRepository() {
    this.data = new HashMap<>();
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    this.data.put(binaryContent.getId(), binaryContent);
    return binaryContent;
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    return Optional.ofNullable(this.data.get(id));
  }

  @Override
  public List<BinaryContent> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
    return findAll().stream()
        .filter(b -> binaryContentIds.contains(b.getId()))
        .toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return data.containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    BinaryContent deletedContent = this.data.remove(id);
  }

  @Override
  public void deleteAll() {
    this.data.clear();
  }

}
