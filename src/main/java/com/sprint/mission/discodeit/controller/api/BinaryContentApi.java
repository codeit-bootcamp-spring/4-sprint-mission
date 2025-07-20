package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "🖇️ Binary Content", description = "Binary Content 관련 API")
public interface BinaryContentApi {
    @Operation(summary = "첨부 파일 조회", description = "바이너리 컨텐츠를 1개 조회합니다.",
            parameters = @Parameter(
                    name        = "binaryContentId",
                    in          = ParameterIn.PATH,
                    description = "조회할 첨부파일 ID (UUID)",
                    required    = true,
                    example     = "7cbe0d73-b1b6-4aca-9006-6c047c2c8b68"
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 조회되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "SuccessExample",
                                    summary = "성공 예시",
                                    value = """
                                                {
                                                     "id": "9682efac-93bb-4975-9ca7-ed566580f466",
                                                     "bytes": "iVBORw0KGgoAAAANSUhE...AAJZCAYAAADmqEFvAA",
                                                     "type": "MESSAGE",
                                                     "fileName": "joy.png",
                                                     "contentType": "image/png",
                                                     "size": 335919,
                                                     "createdAt": "2025-07-20T14:28:57.320479Z"
                                                 }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "해당 바이너리 컨텐츠(binaryContentId)가 존재하지 않습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "NotFound Example",
                                    summary = "NotFound - 해당 바이너리 컨텐츠(binaryContentId)가 존재하지 않은 경우 예시",
                                    value = """
                                                {
                                                    "success": false,
                                                    "code": 404,
                                                    "message": "Not Found Exception",
                                                    "data": "User with id 55e3a449-2c32-4432-8d0d-28620130a8af not found",
                                                    "timestamp": "2025-07-09T16:18:18.43085"
                                                }
                                            """
                            )
                    ))
    })
    ResponseEntity<BinaryContentResponseDto> getBinaryContent(UUID binaryContentId);

    @Operation(
            summary     = "여러 첨부 파일 조회",
            description = "쉼표(,) 로 구분된 binaryContentIds 쿼리 파라미터를 받아 여러 건을 조회합니다.",
            parameters = {
                    @Parameter(
                            name = "binaryContentIds",
                            in   = ParameterIn.QUERY,
                            description = "조회할 첨부 파일 ID 목록 (UUID)",
                            required = true,
                            schema = @Schema(
                                    type  = "array",
                                    // example 에는 JSON array 형태로라도 넣어주면 UI 상에 배열 예시가 뜹니다.
                                    example = "[\"9682efac-93bb-4975-9ca7-ed566580f466\", \"4ca2df6b-4cba-4e7c-ab60-894bd9790715\"]"
                            ),
                            style   = ParameterStyle.FORM,
                            explode = Explode.FALSE    // explode=false 이면 쉼표(CSV) 구분으로 인식
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 조회되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "SuccessExample",
                                    summary = "성공 예시",
                                    value = """
                                            [
                                                {
                                                    "id": "9682efac-93bb-4975-9ca7-ed566580f466",
                                                    "bytes": "iVBORw0KGgoAAAANSUhEUgA...AElFTkSuQmCC",
                                                    "type": "MESSAGE",
                                                    "fileName": "joy.png",
                                                    "contentType": "image/png",
                                                    "size": 335919,
                                                    "createdAt": "2025-07-20T14:28:57.320479Z"
                                                },
                                                {
                                                    "id": "4ca2df6b-4cba-4e7c-ab60-894bd9790715",
                                                    "bytes": "iVBORw0KGgoAAAANSUhEU...AAASUVORK5CYII=",
                                                    "type": "MESSAGE",
                                                    "fileName": "1.png",
                                                    "contentType": "image/png",
                                                    "size": 21224,
                                                    "createdAt": "2025-07-14T05:05:21.590644Z"
                                                }
                                            ]
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "해당 첨부 파일(binaryContentId)이 존재하지 않습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "NotFound Example",
                                    summary = "NotFound - 해당 첨부파일(binaryContentId)이 존재하지 않은 경우 예시",
                                    value = """
                                                {
                                                    "success": false,
                                                    "code": 404,
                                                    "message": "Not Found Exception",
                                                    "data": "User with id 55e3a449-2c32-4432-8d0d-28620130a8af not found",
                                                    "timestamp": "2025-07-09T16:18:18.43085"
                                                }
                                            """
                            )
                    ))
    })
     ResponseEntity<List<BinaryContentResponseDto>> getBinaryContents(List<UUID> binaryContentIds);
}
