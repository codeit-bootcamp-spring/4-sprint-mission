package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.message.*;
import com.sprint.mission.discodeit.entity.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class MessageMapper {

    public Message messageCreateDtoToMessage(MessageCreateDto messageCreateDto) {
        return new Message(
                messageCreateDto.content(), messageCreateDto.userId(), messageCreateDto.channelId());
    }

    public MessageResponseDto messageToMessageResponseDto(Message message) {
        return new MessageResponseDto(
                message.getId(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getContents(),
                message.getState(),
                message.getAttachmentIds());
    }

    public MessageCreateDto messageMultipartDtoToMessageCreateDto(MessageMultipartDto dto) {
        List<BinaryContentRequestDto> binaryContentDtos = new ArrayList<>();
        if (dto.binaryContents() != null && !dto.binaryContents().isEmpty()) {
            try {
                for (MultipartFile binaryContent : dto.binaryContents()) {
                    BinaryContentRequestDto binaryContentDto =
                            new BinaryContentRequestDto(binaryContent.getBytes(), binaryContent.getContentType());
                    binaryContentDtos.add(binaryContentDto);
                }
            } catch (IOException e) {
                binaryContentDtos = null;
            }
        }
        return new MessageCreateDto(dto.content(), binaryContentDtos, dto.userId(), dto.channelId());
    }

    public MessageUpdateDto messageMultipartUpdateDtoToMessageUpdateDto(
            MessageMultipartUpdateDto dto) {
        List<BinaryContentRequestDto> newAttachmentDtos = new ArrayList<>();
        if (dto.newAttachments() != null && !dto.newAttachments().isEmpty()) {
            try {
                for (MultipartFile multipartFile : dto.newAttachments()) {
                    if (multipartFile != null && !multipartFile.isEmpty()) { // 파일이 null이거나 비어있지 않은지 확인
                        BinaryContentRequestDto binaryContentDto =
                                new BinaryContentRequestDto(
                                        multipartFile.getBytes(), multipartFile.getContentType());
                        newAttachmentDtos.add(binaryContentDto);
                    }
                }
            } catch (IOException e) {
                newAttachmentDtos = null;
            }
        }

        return new MessageUpdateDto(
                dto.id(), dto.content(), dto.removeAttachments(), newAttachmentDtos);
    }
}
