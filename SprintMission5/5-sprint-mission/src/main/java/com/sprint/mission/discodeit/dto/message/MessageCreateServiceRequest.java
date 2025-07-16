package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record MessageCreateServiceRequest(
    String content,
    UUID channelId,
    UUID authorId,
    List<MultipartFile> image) {

}
