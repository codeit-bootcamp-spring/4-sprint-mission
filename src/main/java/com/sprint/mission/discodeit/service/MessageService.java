package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateServiceRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateServiceRequest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    // 생성
    MessageResponse createMessage(MessageCreateServiceRequest messageCreateService);

    //조회
    MessageResponse findMessageById(UUID messageId);

    PageResponse<MessageResponse> findMessagesByChannelId(UUID channelId, Instant cursor, Pageable pageable);

    List<MessageResponse> findMessagesByUserId(UUID userId);

    // 수정
    MessageResponse updateContent(MessageUpdateServiceRequest request);

    // 삭제
    void deleteMessage(UUID messageId);
}
