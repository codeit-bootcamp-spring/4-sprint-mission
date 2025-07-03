package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponseDto createMessage(MessageCreateRequestDto messageCreateRequestDTO) throws IOException {

        User user = findUserByUserId(messageCreateRequestDTO.getUserResponseDto().getUserId());
        Channel channel = findChannelByChannelName(messageCreateRequestDTO.getChannelName());

        String contents = messageCreateRequestDTO.getMessageContents();


        if(user.getStatus().equals(UserActivationState.DEACTIVE)) {
            //System.out.printf("'%s' 비활성 상태라 메세지를 작성할 수 없습니다.", user.getUserName());
            return null;
        }

        Message newMessage = new Message(user, channel, contents);
        newMessage.registerMessageToUserAndChannel(user, channel);


        List<MultipartFile> extraContentsFiles = messageCreateRequestDTO.getExtraContentsFiles();
        saveExtraFiles(extraContentsFiles);

        if (messageCreateRequestDTO.getExtraContentsFiles() != null){

            for (MultipartFile file : messageCreateRequestDTO.getExtraContentsFiles()) {
                BinaryContent newBinaryContent = getBinaryContent(file);
                newMessage.addBinaryContentId(newBinaryContent.getId());
                newBinaryContent.setReferenceId(newMessage.getId());

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

        return new MessageResponseDto(newMessage);
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

        User user = findUserByUserId(deleteMessageRequestDTO.getUserResponseDto().getUserId());
        Message message = findMessageByMessageId(deleteMessageRequestDTO.getMessageId());
        Channel channel = findChannelByChannelId(message.getChannelId());

        user.getMessageIds().remove(message.getId());
        channel.getMessageIds().remove(message.getId());

        for (UUID binaryContentId : message.getBinaryContentIds()) {
            binaryContentRepository.deleteBinaryContentByBinaryContentId(binaryContentId);
        }

        userRepository.updateUser(user);
        channelRepository.updateChannel(channel);

        messageRepository.deleteMessageByMessageId(message.getId());
    }

    @Override
    public List<MessageResponseDto> findAllMessage(){
        List<MessageResponseDto> messageResponseDtoList = new ArrayList<>();

        for (Message message : messageRepository.loadMessages()){
            messageResponseDtoList.add(new MessageResponseDto(message));
        }
        return messageResponseDtoList;
    }

    @Override
    public List<MessageResponseDto> findMessagesByChannelId(UUID channelId){
        List<MessageResponseDto> messageResponseDtoList = new ArrayList<>();
        for (Message message : messageRepository.findMessagesByChannelId(channelId)){
            messageResponseDtoList.add(new MessageResponseDto(message));
        }
        return messageResponseDtoList;
    }


    @Override
    public MessageResponseDto updateMessage(MessageUpdateRequestDto messageUpdateRequestDTO) {
        Message message = findMessageByMessageId(messageUpdateRequestDTO.getMessageId());

        User user = findUserByUserId(messageUpdateRequestDTO.getUserResponseDto().getUserId());

        if (user.getStatus().equals(UserActivationState.DEACTIVE)) {
            //System.out.printf("'%s' 비활성 상태라 메세지를 업데이트할 수 없습니다.", user.getUserName());
            return null;
        }
        if (!user.getId().equals(message.getAuthorId())) {
            //System.out.printf("'%s'는 '%s' 메시지의 주인이 아닙니다. 따라서 해당 메시지를 '%s'로 바꾸는 것은 불가능합니다.", user.getUserName(), message.getMessageContents(), newContents);
            return null ;
        }

        message.setMessageContents(messageUpdateRequestDTO.getNewContent());

        for (UUID binaryContentId : message.getBinaryContentIds()) {
            binaryContentRepository.deleteBinaryContentByBinaryContentId(binaryContentId);
        }
        message.clearBinaryContentId();

        List<MultipartFile> extraContentsFiles = messageUpdateRequestDTO.getExtraContentsFiles();
        saveExtraFiles(extraContentsFiles);

        if (messageUpdateRequestDTO.getExtraContentsFiles() != null){
            for (MultipartFile file : messageUpdateRequestDTO.getExtraContentsFiles()) {
                BinaryContent newBinaryContent = getBinaryContent(file);
                binaryContentRepository.createBinaryContent(newBinaryContent);
            }
        }

        messageRepository.updateMessage(message);
        return new MessageResponseDto(message);
    }


    private Channel findChannelByChannelId(UUID channelId) {
        return channelRepository.findChannelByChannelId(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 채널을 찾을 수 없습니다."));
    }

    private Message findMessageByMessageId(UUID messageId) {
        return messageRepository.findMessageByMessageId(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 메시지를 찾을 수 없습니다."));
    }

    private User findUserByUserId(UUID userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저를 찾을 수 없습니다."));
    }

    private Channel findChannelByChannelName(String channelName) {
        return channelRepository.findChannelByChannelName(channelName)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 채널을 찾을 수 없습니다."));
    }

    // 1. 파일 저장 메서드
    private void saveExtraFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

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