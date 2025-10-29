package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MessageResponse {

    private final UUID id;

    private Instant createdAt;

    private Instant updatedAt;

    private String content;

    private UUID channelId;

    private UserResponse author;

    private List<BinaryContentResponse> attachments;

}
