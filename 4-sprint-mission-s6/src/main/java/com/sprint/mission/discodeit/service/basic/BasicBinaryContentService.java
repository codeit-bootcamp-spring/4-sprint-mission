package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDownloadResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    UUID contentId = UUID.randomUUID();
    binaryContentStorage.put(contentId, request.bytes());

    BinaryContent content = new BinaryContent(
            contentId,
            request.fileName(),
            (long) request.bytes().length,
            request.contentType()
    );

    BinaryContent saved = binaryContentRepository.save(content);

    return binaryContentMapper.toDto(saved);
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    BinaryContent content = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> notFound(binaryContentId));

    return binaryContentMapper.toDto(content);
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
            .map(binaryContentMapper::toDto)
            .toList();
  }

  @Override
  public void delete(UUID binaryContentId) {
    BinaryContent find = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> notFound(binaryContentId));

    binaryContentRepository.delete(find);
  }

  @Override
  public BinaryContentDownloadResponse download(UUID binaryContentId) {
    BinaryContent content = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> notFound(binaryContentId));

    return binaryContentMapper.toDownloadResponse(content);
  }

  private NoSuchElementException notFound(UUID id) {
    return new NoSuchElementException("BinaryContent with id " + id + " not found");
  }
}
