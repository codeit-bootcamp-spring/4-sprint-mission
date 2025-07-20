package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageMapper {
    /**
     * MessageCreateDto → Message 엔티티 변환
     */
    public Message toEntity(MessageCreateDto dto) {
        return new Message(
                dto.getContent(),
                dto.getChannelId(),
                dto.getAuthorId(),
                null
        );
    }

    /**
     * MessageUpdateDto → 기존 Message 엔티티 덮어쓰기
     */
    public void updateEntity(MessageUpdateDto dto, Message msg) {
        if (dto.getContent() != null && !dto.getContent().isBlank()) msg.updateContent(dto.getContent());
        msg.touch();
    }

    /**
     * Message →  MessageResponseDto 변환
     */
    public MessageResponseDto toDto(Message msg) {
        return new MessageResponseDto(
                msg.getId(),
                msg.getChannelId(),
                msg.getAuthorId(),
                msg.getContent(),
                msg.getAttachmentIds(),
                msg.getCreatedAt(),
                msg.getUpdatedAt()
        );
    }

    /**
     * Message → MessageResponseDto 를 리스트로 변환
     */
    public List<MessageResponseDto> toDtoList(List<Message> messages) {
        return messages.stream()
                .map(this::toDto)
                .toList();
    }

}
