package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateServiceRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateServiceRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final BasicBinaryContentService basicBinaryContentService;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final MessageAttachmentRepository messageAttachmentRepository;

    @Override
    public MessageDto createMessage(MessageCreateServiceRequest request) {
        User author = userRepository.findById(request.authorId())
            .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
        Channel channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));

        Message message = new Message(request.content(), author, channel);
        messageRepository.save(message);

        List<MultipartFile> imageFiles = request.image();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            imageFiles.stream()
                .filter(multipartFile -> multipartFile != null && !multipartFile.isEmpty())
                .forEach(file -> {
                  String fileName = file.getOriginalFilename();
                  String contentType = file.getContentType();
                    BinaryContentDto created = basicBinaryContentService.createBinaryContent(
                        new BinaryContentCreateServiceRequest(message.getId(), null,fileName,contentType, file));
                    byte[] decodedBytes = null;
                    if (created.bytes() != null && !created.bytes().isEmpty()) {
                        decodedBytes = Base64.getDecoder().decode(created.bytes());
                    }
                    binaryContentStorage.put(created.id(), decodedBytes);

                    BinaryContent attachment = binaryContentMapper.binaryContentDtoToBinaryContent(created);

                    MessageAttachment messageAttachment = new MessageAttachment();
                    messageAttachment.setMessage(message);
                    messageAttachment.setAttachment(attachment);
                    message.getMessageAttachments().add(messageAttachment);
                });
        }

        return messageMapper.messageToMessageDto(message, toBinaryContentDto(message));
    }


    @Override
    public MessageDto updateMessage(UUID messageId, MessageUpdateServiceRequest request) {
        Message message =
                messageRepository
                        .findById(messageId)
                        .orElseThrow(() -> new MessageNotFoundException("해당 메시지를 찾을 수 없습니다."));

        if (request.newContent() != null && !request.newContent().equals(message.getContent())) {
            message.setContent(request.newContent());
        }

        return messageMapper.messageToMessageDto(message, toBinaryContentDto(message));
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException("메시지를 찾을 수 없습니다."));

        List<MessageAttachment> messageAttachments = messageAttachmentRepository.findAllByMessageId(messageId);
        List<BinaryContent> attachments = messageAttachments.stream()
            .map(MessageAttachment::getAttachment)
            .collect(Collectors.toList());

        messageAttachmentRepository.deleteAll(messageAttachments);
        binaryContentRepository.deleteAll(attachments);

        messageRepository.delete(message);
    }


  @Override
  public Page<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
    return messageRepository.findAllByChannelId(channelId, pageable)
        .map(message -> messageMapper.messageToMessageDto(message, toBinaryContentDto(message)));
  }

  private List<BinaryContentDto> toBinaryContentDto(Message message) {
        return message.getMessageAttachments().stream()
            .map(MessageAttachment::getAttachment)
            .map(attachment -> {
                try (InputStream in = binaryContentStorage.get(attachment.getId())) {
                    byte[] bytes = in.readAllBytes();
                    String encoded = Base64.getEncoder().encodeToString(bytes);
                    return binaryContentMapper.binaryContentToBinaryContentDto(attachment, encoded);
                } catch (IOException e) {
                    throw new RuntimeException("BinaryContent 로드 실패: " + attachment.getId(), e);
                }
            })
            .collect(Collectors.toList());
    }

}
