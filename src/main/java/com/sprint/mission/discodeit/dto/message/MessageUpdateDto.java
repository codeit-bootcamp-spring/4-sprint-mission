package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// UserPatchDto로 PATCH 시 부분 수정만 되도록 설정했습니다
// 그러기 위해서 @NoArgsConstructor, @Setter 추가해서 imageFile 없을 땐 null로 바인딩되게 했습니다.
@Getter @Setter
@NoArgsConstructor
public class MessageUpdateDto {
    @Schema(description = "메시지 내용", example = "3번째가 아니라 4번째 공지 확인해주세요. 확인 후 ✅ 이모지 눌러주세요!")
    private String content;

    @Schema(description = "첨부파일 리스트")
    private List<MultipartFile> attachments;

    @Schema(description = "첨부파일 타입", example = "MESSAGE")
    private BinaryContentType attachmentType;
}
