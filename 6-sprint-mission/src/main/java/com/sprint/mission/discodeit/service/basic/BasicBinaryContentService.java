package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentStorage binaryContentStorage;
  private static final Logger log = LoggerFactory.getLogger(BasicBinaryContentService.class);

  @Override
  public BinaryContentDto createBinaryContent(BinaryContentCreateServiceRequest request) {
    log.info("[REQ] messageId: {}, userId: {}", request.messageId(), request.userId());
    try {
      MultipartFile file = request.file();
      if (file == null || file.isEmpty()) {
        throw new IllegalArgumentException("파일이 비어 있습니다.");
      }

      BinaryContent binaryContent = binaryContentMapper.binaryContentCreateServiceRequestToBinaryContent(request);

      log.info("[PUT] 파일 저장 시도 - UUID: {}", binaryContent.getId());
      binaryContentStorage.put(binaryContent.getId(), file.getBytes());
      log.info("[PUT] 파일 저장 완료 - UUID: {}", binaryContent.getId());

      BinaryContent saved = binaryContentRepository.save(binaryContent);

      if (request.userId() != null) {
        userRepository.findById(request.userId()).ifPresent(user -> {
          user.setProfile(saved);
          userRepository.save(user);
        });
      }

      if (request.messageId() != null) {
        messageRepository.findById(request.messageId()).ifPresent(message -> {
          message.addAttachment(saved);
          messageRepository.save(message);
        });
      }

      String base64 = Base64.getEncoder().encodeToString(file.getBytes());
      return binaryContentMapper.binaryContentToBinaryContentDto(saved, base64);

    } catch (IOException e) {
      throw new RuntimeException("파일 저장 실패", e);
    }
  }

  @Override
  public BinaryContentDto findBinaryContentById(UUID id) {
    return binaryContentRepository.findById(id)
        .map(binaryContent -> {
          byte[] content = readBinaryContent(id);
          String encoded = Base64.getEncoder().encodeToString(content);
          return binaryContentMapper.binaryContentToBinaryContentDto(binaryContent, encoded);
        })
        .orElseThrow(() -> new IllegalArgumentException("해당 ID와 일치하는 binaryContent가 없습니다."));
  }

    @Override
    public void deleteBinaryContent(UUID id) {
      BinaryContent binaryContent = binaryContentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("삭제할 binaryContent가 없습니다."));
      binaryContentRepository.delete(binaryContent);
    }

    @Override
    public List<BinaryContentDto> findAllBinaryContentById(UUID id) {
      if (userRepository.existsById(id)) {
        return findAllBinaryContentByUserId(id);
      } else if (messageRepository.existsById(id)) {
        return findAllBinaryContentByMessageId(id);
      }
      throw new IllegalArgumentException("해당 ID와 일치하는 유저 또는 메시지가 존재하지 않습니다.");
    }

  private List<BinaryContentDto> findAllBinaryContentByUserId(UUID userId) {
    return userRepository.findById(userId).stream()
        .map(User::getProfile)
        .map(binaryContent -> {
          byte[] content = readBinaryContent(binaryContent.getId());
          String encoded = Base64.getEncoder().encodeToString(content);
          return binaryContentMapper.binaryContentToBinaryContentDto(binaryContent, encoded);
        })
        .collect(Collectors.toList());
  }

  private List<BinaryContentDto> findAllBinaryContentByMessageId(UUID messageId) {
    return messageRepository.findById(messageId)
        .map(Message::getMessageAttachments)
        .orElse(List.of())
        .stream()
        .map(MessageAttachment::getAttachment)
        .map(binaryContent -> {
          byte[] content = readBinaryContent(binaryContent.getId());
          String encoded = Base64.getEncoder().encodeToString(content);
          return binaryContentMapper.binaryContentToBinaryContentDto(binaryContent, encoded);
        })
        .collect(Collectors.toList());
  }


  private byte[] readBinaryContent(UUID id) {
    try (InputStream in = binaryContentStorage.get(id)) {
      return in.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException("파일을 로드할 수 없습니다.", e);
    }
  }
}
