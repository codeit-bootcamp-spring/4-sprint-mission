package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;

  @Override
  public BinaryContent createBinaryContent(
      BinaryContentCreateServiceRequest request) {
    BinaryContentCreateDto createDto = new BinaryContentCreateDto(
        request.userId(),
        request.messageId(),
        request.file().getOriginalFilename(),
        request.file().getSize(),
        request.file().getContentType(),
        request.file()
    );
    BinaryContentEntity binaryContentEntity =
        binaryContentRepository.save(
            binaryContentMapper.BinaryContentCreateDtoToBinaryContent(createDto));
    return binaryContentMapper.BinaryContentToBinaryContentResponseDto(binaryContentEntity);
  }

  @Override
  public BinaryContent findBinaryContentById(UUID id) {
    return binaryContentRepository
        .findById(id)
        .map(binaryContentMapper::BinaryContentToBinaryContentResponseDto)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID와 일치하는 binaryContent가 없습니다."));
  }

  @Override
  public List<BinaryContent> findAllBinaryContentByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAll().stream()
        .filter(binaryContent -> ids.contains(binaryContent.getId()))
        .map(binaryContentMapper::BinaryContentToBinaryContentResponseDto)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteBinaryContent(UUID id) {
    binaryContentRepository
        .findById(id)
        .ifPresentOrElse(
            binaryContent -> {
              binaryContentRepository.delete(id);
            },
            () -> {
              throw new IllegalArgumentException("삭제할 binaryContent가 없습니다.");
            });
  }

  @Override
  public List<BinaryContent> findAllBinaryContentById(UUID id) {

    if (userRepository.findById(id).isPresent()) {
      return findAllBinaryContentByUserId(id);
    }

    if (messageRepository.findById(id).isPresent()) {
      return findAllBinaryContentByMessageId(id);
    }

    throw new IllegalArgumentException("해당 ID와 일치하는 유저 또는 메시지가 존재하지 않습니다.");
  }

  private List<BinaryContent> findAllBinaryContentByUserId(UUID userId) {
    return binaryContentRepository.findAll().stream()
        .filter(b -> userId.equals(b.getUserId()))
        .map(binaryContentMapper::BinaryContentToBinaryContentResponseDto)
        .collect(Collectors.toList());
  }

  private List<BinaryContent> findAllBinaryContentByMessageId(UUID messageId) {
    return binaryContentRepository.findAll().stream()
        .filter(b -> messageId.equals(b.getMessageId()))
        .map(binaryContentMapper::BinaryContentToBinaryContentResponseDto)
        .collect(Collectors.toList());
  }
}