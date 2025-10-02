package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.exception.message.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    BasicMessageService messageService;

    @Nested
    @DisplayName("create()")
    class CreateMessageTest {

        @Test
        @DisplayName("메세지 생성 중 삽입하려는 첨부 파일 ID가 레포지토리에 없으면 예외 발생")
        void create_shouldThrow_whenAttachmentsNotFound() {
            //given
            MessageCreateDto dto = mock(MessageCreateDto.class);
            UUID missingId = UUID.randomUUID();
            when(dto.getContent()).thenReturn("hello");
            when(dto.getAttachmentId()).thenReturn(List.of(missingId));
            when(binaryContentRepository.findById(missingId)).thenReturn(Optional.empty());

            //when & then
            assertThatThrownBy(()-> messageService.create(dto))
                    .isInstanceOf(BinaryContentNotFoundException.class);

        }
    }
}
