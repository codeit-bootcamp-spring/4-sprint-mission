package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class MessageCreateDto {
    private UUID profileId;
    private UUID authorId;
    private UUID channelId;
    private String content;
    private List<MultipartFile> attachments;
}
