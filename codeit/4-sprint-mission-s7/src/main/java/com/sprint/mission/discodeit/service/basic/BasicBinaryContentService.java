package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.BytesEmptyException;
import com.sprint.mission.discodeit.exception.binarycontent.ContentTypeBlankException;
import com.sprint.mission.discodeit.exception.binarycontent.FileNameBlankException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    log.info("Create BinaryContent requested: fileName='{}', contentType={}", fileName, contentType);

    if (fileName == null || fileName.isBlank()) {
      log.debug("Create BinaryContent rejected: blank fileName");
      throw new FileNameBlankException(fileName);
    }
    if (bytes == null || bytes.length == 0) {
      log.debug("Create BinaryContent rejected: empty bytes");
      throw new BytesEmptyException(bytes);
    }
    if (contentType == null || contentType.isBlank()) {
      log.debug("Create BinaryContent rejected: blank contentType");
      throw new ContentTypeBlankException(contentType);
    }

    BinaryContent binaryContent = new BinaryContent(
            fileName,
            (long) bytes.length,
            contentType
    );
    binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(binaryContent.getId(), bytes);

    log.info("Create BinaryContent succeeded: id={}, fileName='{}', size={}",
            binaryContent.getId(), fileName, bytes.length);
    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    log.info("Find BinaryContent requested: id={}", binaryContentId);

      return binaryContentRepository.findById(binaryContentId)
              .map(binaryContentMapper::toDto)
              .map(BinaryContentDto -> {
                log.info("Find BinaryContent succeeded: id={}", binaryContentId);
                return BinaryContentDto;
              })
              .orElseThrow(() -> {
                BinaryContentNotFoundException binaryContentNotFoundException = new BinaryContentNotFoundException(binaryContentId);
                        log.debug("Find BinaryContent rejected: not found: id={}", binaryContentId, binaryContentNotFoundException);
                        return binaryContentNotFoundException;
                      }
              );
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {

    log.info("FindAll BinaryContent requested: count={}", binaryContentIds.size());

      List<BinaryContentDto> list = binaryContentRepository.findAllById(binaryContentIds).stream()
              .map(binaryContentMapper::toDto)
              .toList();

    log.info("FindAll BinaryContent succeeded: requestedCount={}, returnedCount={}",
            binaryContentIds.size(), list.size());

      return list;
  }

  @Transactional
  @Override
  public void delete(UUID binaryContentId) {

    log.info("Delete BinaryContent requested: id={}", binaryContentId);

    if (!binaryContentRepository.existsById(binaryContentId)) {

      BinaryContentNotFoundException binaryContentNotFoundException = new BinaryContentNotFoundException(binaryContentId);
      log.debug("Delete BinaryContent rejected: not found: id={}", binaryContentId, binaryContentNotFoundException);
      throw binaryContentNotFoundException;
    }
    binaryContentRepository.deleteById(binaryContentId);
    log.info("Delete BinaryContent succeeded: id={}", binaryContentId);
  }
}
