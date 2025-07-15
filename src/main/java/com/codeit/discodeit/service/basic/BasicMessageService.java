package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequestDto;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.exception.NoFindChannelException;
import com.codeit.discodeit.exception.exception.NoFindMessageException;
import com.codeit.discodeit.exception.exception.NoFindUserException;
import com.codeit.discodeit.repository.BinaryContentRepository;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.MessageRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.MessageService;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public Message createMessage(MessageCreateRequest messageCreateRequest,
      List<MultipartFile> attachments) {

    User user = findUserByUserId(messageCreateRequest.getAuthorId());
    Channel channel = findChannelByChannelId(messageCreateRequest.getChannelId());
    String contents = messageCreateRequest.getContent();

    Message newMessage = new Message(user, channel, contents);
    newMessage.registerMessageToUserAndChannel(user, channel);

    saveExtraFiles(attachments);

    if (attachments != null) {

      for (MultipartFile file : attachments) {
        BinaryContent newBinaryContent = getBinaryContent(file);
        newMessage.addBinaryContentId(newBinaryContent.getId());
        newBinaryContent.setReferenceId(newMessage.getId());

        newBinaryContent.setContentType(file.getContentType());
        newBinaryContent.setFileName(file.getOriginalFilename());
        newBinaryContent.setSize((int) file.getSize());

        binaryContentRepository.createBinaryContent(newBinaryContent);
      }
    }

    messageRepository.createMessage(newMessage);

    User updateMessageUser = findUserByUserId(newMessage.getAuthorId());
    updateMessageUser.addMessage(newMessage);
    userRepository.updateUser(updateMessageUser);

    Channel updateMessageChannel = findChannelByChannelId(newMessage.getChannelId());
    updateMessageChannel.addMessage(newMessage);
    channelRepository.updateChannel(updateMessageChannel);

    return newMessage;
  }

  private static BinaryContent getBinaryContent(MultipartFile file) {
    String filePath = "./src/main/resources/extraContentsFile/" + file.getOriginalFilename();
    byte[] extraFileBytes = null;
    try (FileInputStream fis = new FileInputStream(filePath)) {
      extraFileBytes = fis.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException("바이트화가 불가능합니다: " + e.getMessage(), e);
    }

    return new BinaryContent(filePath, BinaryContentType.MESSAGE_ATTACHMENT, extraFileBytes);
  }

  @Override
  public void deleteMessage(DeleteMessageRequestDto deleteMessageRequestDTO) {

    Message message = findMessageByMessageId(deleteMessageRequestDTO.getMessageId());
    User user = findUserByUserId(message.getAuthorId());
    Channel channel = findChannelByChannelId(message.getChannelId());

    user.getMessageIds().remove(message.getId());
    channel.getMessageIds().remove(message.getId());

    for (UUID binaryContentId : message.getAttachmentIds()) {
      binaryContentRepository.deleteBinaryContentByBinaryContentId(binaryContentId);
    }

    userRepository.updateUser(user);
    channelRepository.updateChannel(channel);

    messageRepository.deleteMessageByMessageId(message.getId());
  }

  @Override
  public List<Message> findMessagesByChannelId(UUID channelId) {
    return messageRepository.findMessagesByChannelId(channelId);
  }


  @Override
  public Message updateMessage(MessageUpdateRequestDto messageUpdateRequestDto) {
    Message message = findMessageByMessageId(messageUpdateRequestDto.getMessageId());

    User user = findUserByUserId(message.getAuthorId());

    if (user.getStatus().equals(UserActivationState.DEACTIVE)) {
      //System.out.printf("'%s' 비활성 상태라 메세지를 업데이트할 수 없습니다.", user.getUserName());
      return null;
    }
    if (!user.getId().equals(message.getAuthorId())) {
      //System.out.printf("'%s'는 '%s' 메시지의 주인이 아닙니다. 따라서 해당 메시지를 '%s'로 바꾸는 것은 불가능합니다.", user.getUserName(), message.getMessageContents(), newContents);
      return null;
    }

    message.setContent(messageUpdateRequestDto.getNewContent());

    for (UUID binaryContentId : message.getAttachmentIds()) {
      binaryContentRepository.deleteBinaryContentByBinaryContentId(binaryContentId);
    }
    message.clearBinaryContentId();

    List<MultipartFile> extraContentsFiles = messageUpdateRequestDto.getExtraContentsFiles();
    saveExtraFiles(extraContentsFiles);

    if (messageUpdateRequestDto.getExtraContentsFiles() != null) {
      for (MultipartFile file : messageUpdateRequestDto.getExtraContentsFiles()) {
        BinaryContent newBinaryContent = getBinaryContent(file);
        binaryContentRepository.createBinaryContent(newBinaryContent);
      }
    }

    messageRepository.updateMessage(message);
    return message;
  }

  private Channel findChannelByChannelId(UUID channelId) {
    return channelRepository.findChannelByChannelId(channelId)
        .orElseThrow(
            () -> new NoFindChannelException("해당하는 채널을 찾을 수 없습니다.", channelId + "can't found"));
  }

  private Message findMessageByMessageId(UUID messageId) {
    return messageRepository.findMessageByMessageId(messageId)
        .orElseThrow(
            () -> new NoFindMessageException("해당하는 메시지를 찾을 수 없습니다.", messageId + "can't found"));
  }

  private User findUserByUserId(UUID userId) {
    return userRepository.findUserById(userId)
        .orElseThrow(() -> new NoFindUserException("해당하는 유저를 찾을 수 없습니다.", userId + "can't found"));
  }

  // 1. 파일 저장 메서드
  private void saveExtraFiles(List<MultipartFile> files) {
    if (files == null || files.isEmpty()) {
      return;
    }

    for (MultipartFile file : files) {
      if (!file.isEmpty()) {
        String originalFileName = file.getOriginalFilename();
        Path savePath = Paths.get("./src/main/resources/extraContentsFile/", originalFileName);
        try {
          Files.createDirectories(savePath.getParent());
          file.transferTo(savePath);
        } catch (IOException e) {
          throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
      }
    }
  }
}