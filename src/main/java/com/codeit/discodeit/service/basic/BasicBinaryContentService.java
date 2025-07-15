package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.exception.exception.NoFindBinaryContent;
import com.codeit.discodeit.repository.BinaryContentRepository;
import com.codeit.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public List<BinaryContent> findBinaryContentsByBinaryContentIds(List<UUID> binaryContentIds) {

    return binaryContentRepository.loadBinaryContents().stream()
        .filter(binaryContent -> binaryContentIds.contains(binaryContent.getId())).toList();
  }

  @Override
  public BinaryContent findBinaryContentByBinaryContentId(UUID binaryContentId) {
    Optional<BinaryContent> binaryContent = binaryContentRepository.findBinaryContentByBinaryContentId(
        binaryContentId);
    if (binaryContent.isEmpty()) {
      throw new NoFindBinaryContent("첨부 파일을 찾을 수 없음",
          "BinaryContent with id {" + binaryContentId + "} not found");
    }
    return binaryContent.get();
  }
}
