package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.codeit.discodeit.repository.BinaryContentRepository;
import com.codeit.discodeit.service.BinaryContentService;
import com.codeit.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public void createByteFile(BinaryContent binaryContent, byte[] bytes) {
    log.info("[createByteFile] 파일 저장 시작: binaryContentId={}, size={}", binaryContent.getId(), bytes.length);
    binaryContentStorage.put(binaryContent.getId(), bytes);
    log.info("[createByteFile] 파일 저장 완료: binaryContentId={}", binaryContent.getId());
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContent> findBinaryContentsByBinaryContentIds(List<UUID> binaryContentIds) {
    return binaryContentRepository.findAllById(binaryContentIds);
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContent findBinaryContentByBinaryContentId(UUID binaryContentId) {
    Optional<BinaryContent> binaryContent = binaryContentRepository.findById(
        binaryContentId);
    if (binaryContent.isEmpty()) {
      Map<String, Object> details = Map.of(
          "이유", "바이너리 컨텐츠 없음"
      );
      throw new BinaryContentNotFoundException(details);
    }
    return binaryContent.get();
  }

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
    log.info("[download] 다운로드 시작: binaryContentId={}, fileName={}",
        binaryContentDto.id(), binaryContentDto.fileName());

    ResponseEntity<?> response = binaryContentStorage.download(binaryContentDto);

    log.info("[download] 다운로드 완료: binaryContentId={}", binaryContentDto.id());
    return response;
  }
}
