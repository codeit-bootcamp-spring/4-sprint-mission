package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class BinaryContentCreateDto {
    @Schema(description = "바이너리 컨텐츠 타입", example = "MESSAGE")
    @NotNull
    private BinaryContentType type;

    @Schema(description="바이너리 파일", type="string", format="binary")
    private MultipartFile file;
}
