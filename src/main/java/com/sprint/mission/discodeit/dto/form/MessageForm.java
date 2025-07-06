package com.sprint.mission.discodeit.dto.form;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record MessageForm(
        String content,
        UUID channelId,
        UUID authorId,
        List<MultipartFile> attachments
) {
}
