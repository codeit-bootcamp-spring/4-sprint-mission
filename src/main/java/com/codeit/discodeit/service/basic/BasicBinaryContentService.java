package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.exception.ErrorCode;
import com.codeit.discodeit.exception.exception.BusinessException;
import com.codeit.discodeit.repository.BinaryContentRepository;
import com.codeit.discodeit.service.BinaryContentService;
import com.codeit.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public void createByteFile(BinaryContent binaryContent, byte[] bytes) {
    binaryContentStorage.put(binaryContent.getId(), bytes);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContent> findBinaryContentsByBinaryContentIds(List<UUID> binaryContentIds) {
    return binaryContentRepository.findBinaryContentListByBinaryContentIds(binaryContentIds);
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContent findBinaryContentByBinaryContentId(UUID binaryContentId) {
    Optional<BinaryContent> binaryContent = binaryContentRepository.findBinaryContentByBinaryContentId(
        binaryContentId);
    if (binaryContent.isEmpty()) {
      throw new BusinessException(ErrorCode.NO_FIND_BINARY_CONTENT);
    }
    return binaryContent.get();
  }

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
    return binaryContentStorage.download(binaryContentDto);
  }
}
