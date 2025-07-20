package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class MessageCreateDto {
    @Schema(description = "메시지 내용", example = "3번째 공지! 필독! ")
    @NotBlank
    private final String content;

    @Schema(description = "채널 ID", example = "a5a0f4f5-ca67-4d3d-ab62-d4907b21ec63")
    @NotNull
    private final UUID channelId;

    @Schema(description = "작성자 ID", example = "82170d94-e9e6-45df-874a-f4fb949f0835")
    @NotNull
    private final UUID authorId;

    @Schema(description = "첨부파일 리스트")
    private final List<MultipartFile> attachments;

    @Schema(description = "첨부파일 타입", example = "MESSAGE")
    private final BinaryContentType attachmentType;
}
