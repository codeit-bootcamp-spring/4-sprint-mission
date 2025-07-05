package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    //
    private final ReadStatusRepository readStatusRepository;

    @Override
    public Message create(MessageDto.create request) {
        UUID channelId = request.getChannelId();
        UUID authorId = request.getAuthorId();
        
        // 채널, 유저 유효성 검사
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널 아이디에 해당하는 채널이 없습니다."));

        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("Author with id " + authorId + " does not exist");
        }
        
        // 비공개 채널 유효성 검사; 가입된 유저만 메시지 작성 가능
        if (channel.getType() == ChannelType.PRIVATE) {
            List<UUID> participantsIds = Optional.ofNullable(readStatusRepository.findAllByChannelId(channelId).stream()
                    .map(ReadStatus::getUserId))
                    .map(Stream::toList)
                    .orElseThrow(() -> new IllegalArgumentException("참여자가 없는 비공개 채널에 메시지를 보낼 수 없습니다."));

           if (!participantsIds.contains(authorId)) {
               throw new IllegalArgumentException("참여하지 않은 비공개 채널에 메시지를 보낼 수 없습니다.");
           }
        }
      
        // 첨부 파일 처리.. 첨부된 파일이 없으면 empty
        List<MultipartFile> files = Optional.ofNullable(request.getFiles())
                .orElse(Collections.emptyList());

        List<UUID> nullableAttachmentIds = files.stream()
                .map(file -> {
                    String fileName = file.getOriginalFilename();
                    String contentType = file.getContentType();
                    try {
                        byte[] bytes = file.getBytes();
                        BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                        BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
                        return createdBinaryContent.getId();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read file: " + fileName, e);
                    }
                })
                .toList();

        Message message = request.toMessage(nullableAttachmentIds);
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("존재하지 않는 채널 아이디입니다.");
        }
        return messageRepository.findAllByChannelId(channelId).stream()
                .toList();
    }

    @Override
    public Message update(UUID messageId, MessageDto.update request) {
        String newContent = request.getNewContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(newContent);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.getAttachmentIds()
                .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(messageId);
    }
}
