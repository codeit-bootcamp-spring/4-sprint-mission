package com.sprint.mission.discodeit.dto.message.request;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class MessageCreateServiceRequest {

    private final String message;

    private final UUID channelId;

    private final UUID userId;

    @Builder.Default
    private final List<MultipartFile> attachments = new ArrayList<>();
}
