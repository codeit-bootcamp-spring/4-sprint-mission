package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.constant.BinaryContentErrorCode;
import com.sprint.mission.discodeit.constant.ChannelErrorCode;
import com.sprint.mission.discodeit.constant.MessageErrorCode;
import com.sprint.mission.discodeit.constant.UserErrorCode;
import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateServiceRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateServiceRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BinaryContentException;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.MessageException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.util.BinaryContentConverter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service("messageService")
@Slf4j
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;

    private final BinaryContentStorage binaryContentStorage;

    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional
    public MessageResponse createMessage(MessageCreateServiceRequest request) {

        User author = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Channel findChannel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));

        Message message = messageMapper.toEntity(request, author, findChannel);

        log.info("message to create - content={}", message.getContent());

        List<MessageAttachment> attachments = new ArrayList<>();

        List<MultipartFile> multipartFiles = Optional.ofNullable(request.getAttachments()).orElse(new ArrayList<>());

        List<BinaryContent> binaryContents = convertMultipartFilesToBinaryContents(multipartFiles);

        binaryContentRepository.saveAll(binaryContents);

        binaryContents.stream()
                        .forEach(binaryContent -> {
                            binaryContentStorage.put(binaryContent.getId(), binaryContent.getBytes());
                        });

        attachments = convertBinaryContentsToMessageAttachment(binaryContents, message);

        message.addAttachments(attachments);

        messageRepository.save(message);
        log.info("message created sucessfully - id={}", message.getId());
        return messageMapper.toResponse(message);
    }

    private BinaryContent getBinaryContentFromMultipartFile(MultipartFile profile) {
        BinaryContent binaryContent;
        try {
            binaryContent = BinaryContentConverter.toBinaryContent(profile);
        } catch(IOException e) {
            throw new BinaryContentException(BinaryContentErrorCode.MULTIPART_FILE_CONVERT_FAILED);
        }
        return binaryContent;
    }

    private List<BinaryContent> convertMultipartFilesToBinaryContents(List<MultipartFile> multipartFiles) {
        return multipartFiles.stream()
                .map(this::getBinaryContentFromMultipartFile)
                .toList();
    }

    private List<MessageAttachment> convertBinaryContentsToMessageAttachment(List<BinaryContent> binaryContents, Message message) {
        return binaryContents.stream()
                .map(binaryContent ->
                        MessageAttachment.builder()
                                .binaryContent(binaryContent)
                                .message(message)
                                .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MessageResponse findMessageById(UUID messageId) {
        Message findMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

        return messageMapper.toResponse(findMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> findMessagesByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
        Slice<MessageResponse> messages = messageRepository.findChannelMessagesByCursor(channelId, Optional.ofNullable(cursor).orElse(Instant.now()), pageable)
                .map(messageMapper::toResponse);

        Instant nextCursor = null;

        if(!messages.getContent().isEmpty()) {
            nextCursor = messages.getContent().get(messages.getContent().size() - 1).getCreatedAt();
        }

        return pageResponseMapper.toPageResponse(messages, nextCursor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> findMessagesByUserId(UUID userId) {
        return messageRepository.findAll()
                .stream()
                .filter(message -> message.getAuthorId().equals(userId))
                .map(messageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MessageResponse updateContent(MessageUpdateServiceRequest request) {
        Optional.ofNullable(request.getContent()).orElseThrow(() -> new MessageException(MessageErrorCode.UPDATE_CONTENT_IS_NULL));

        Message messageToUpdate = messageRepository.findById(request.getMessageId())
               .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

        log.info("message to update - content={}", messageToUpdate.getContent());
        messageToUpdate.editContent(request.getContent());

        messageRepository.save(messageToUpdate);

        log.info("message updated - content={}", messageToUpdate.getContent());
        return messageMapper.toResponse(messageToUpdate);
    }

    @Override
    @Transactional
    public void deleteMessage(UUID messageId) {
        messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

        removeMessage(messageId);
        log.info("message deleted - id={}", messageId);
    }

    private void removeBinaryContentsOfMessage(List<BinaryContent> binaryContents) {
        if(!binaryContents.isEmpty()) {
            for (BinaryContent content : binaryContents) {
                binaryContentRepository.deleteById(content.getId());
            }
        }
    }

    private void removeMessage(UUID messageId) {
        messageRepository.findById(messageId)
                .ifPresentOrElse(
                        message -> messageRepository.deleteById(messageId),
                        () -> { throw new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND); }
                );
    }

    public boolean isAuthor(UUID authorId, UUID messageId) {
        return messageRepository.findById(messageId)
                .map(message -> message.getAuthorId().equals(authorId))
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));
    }
}
