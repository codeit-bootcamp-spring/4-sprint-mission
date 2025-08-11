package com.codeit.discodeit.repository.jpa_repository;

import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.repository.BinaryContentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class JpaBinaryContentRepository implements BinaryContentRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public void createBinaryContent(BinaryContent contents){
    em.persist(contents);
  }

  @Override
  public void deleteBinaryContent(BinaryContent binaryContent){
    em.remove(binaryContent);
  }

  @Override
  public Optional<BinaryContent> findBinaryContentByBinaryContentId(UUID binaryContentsId){
    return Optional.ofNullable(em.find(BinaryContent.class, binaryContentsId));
  }

  @Override
  public List<BinaryContent> findBinaryContentListByBinaryContentIds(List<UUID> binaryContentIds){
    if (binaryContentIds == null || binaryContentIds.isEmpty()) {
      return Collections.emptyList();
    }
    return em.createQuery("SELECT bc FROM BinaryContent bc WHERE bc.id in :binaryContentIds", BinaryContent.class)
        .setParameter("binaryContentIds", binaryContentIds)
        .getResultList();
  }
}
