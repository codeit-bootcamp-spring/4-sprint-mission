package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.response.ResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @Operation(summary = "첨부 파일 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  {
                      "fileName": "profile.png",
                      "size": 23771,
                      "contentType": "image/png",
                      "bytes":\s
                  }
                  """)
          )),
      @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  BinaryContent with id {binaryContentId} not found
                  """)
          ))
  })
  @RequestMapping(path = "/api/binary-contents/{binary-content-id}", method = RequestMethod.GET)
  public ResponseEntity<BinaryContent> find(
      @PathVariable("binary-content-id") UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContent);
  }

  @Operation(summary = "여러 첨부 파일 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  [
                    {
                          "fileName": "profile.png",
                          "size": 23771,
                          "contentType": "image/png",
                          "bytes":\s
                    }
                  ]
                  """)
          ))
  })
  @RequestMapping(path = "/api/binary-contents", method = RequestMethod.GET)
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam List<UUID> binaryContentId) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }
}
