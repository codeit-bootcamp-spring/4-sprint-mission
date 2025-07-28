package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @Transactional
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    BinaryContent binaryContent = binaryContentMapper.mapToEntity(request);

    return binaryContentMapper.mapToDto(binaryContentRepository.save(binaryContent));
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDto find(UUID binaryContentId) {
    return binaryContentMapper.mapToDto(getFindingBinaryContent(binaryContentId));
  }

  // !!!!!!!!!!페이징
  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentMapper.mapToDtoList(binaryContentRepository.findAllByIdIn(binaryContentIds));
  }

  @Override
  public void delete(UUID binaryContentId) {
    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found");
    }
    binaryContentRepository.deleteById(binaryContentId);
  }

  // 공통 메서드
  private BinaryContent getFindingBinaryContent(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));
  }
}
