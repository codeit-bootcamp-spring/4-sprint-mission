package com.sprint.mission.discodeit.dto.binarycontent.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BinaryContentCreateRequest {

    @Length(min = 1, max = 255, message = "파일 이름은 1 ~ 255자 이어야 함")
    @NotBlank(message = "fileName는 null이거나 공백이면 안됨")
    private String fileName;

    @Length(min = 1, max = 100, message = "contentTyped은 1 ~ 100자 이어야 함")
    @NotNull(message = "contentType는 null이면 안됨")
    private String contentType;

    @NotNull(message = "data는 null이면 안됨")
    private byte[] bytes;

    public BinaryContentCreateServiceRequest toServiceRequest() {
        return BinaryContentCreateServiceRequest.builder()
                .fileName(fileName)
                .contentType(contentType)
                .bytes(bytes)
                .build();
    }
}
