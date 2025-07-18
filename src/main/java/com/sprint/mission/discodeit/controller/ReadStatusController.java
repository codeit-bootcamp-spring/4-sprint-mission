package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.response.ResponseDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @Operation(summary = "Message 읽음 상태 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  Channel | User with id {channelId | userId} not found
                  """)
          )),
      @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  ReadStatus with userId {userId} and channelId {channelId} already exists
                  """)
          )),
      @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  {
                      "userId": "59cdbeac-c530-47d8-905c-90fdaaf91884",
                      "channelId": "e3d222da-e7fd-40a1-a835-37bed9bbb6cf",
                      "lastReadAt": "2025-07-15T14:58:00Z",
                      "id": "52623bdc-fb65-4315-9c2b-d85856ccf54d",
                      "createdAt": "2025-07-15T11:54:42.141506Z",
                      "updatedAt": null
                  }
                  """)
          ))
  })
  @RequestMapping(value = "/api/read-statuses", method = RequestMethod.POST)
  public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
    ReadStatus createdReadStatus = readStatusService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdReadStatus);
  }

  @Operation(summary = "Message 읽음 상태 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  {
                      "userId": "59cdbeac-c530-47d8-905c-90fdaaf91884",
                      "channelId": "e3d222da-e7fd-40a1-a835-37bed9bbb6cf",
                      "lastReadAt": "2025-07-15T10:00:00Z",
                      "id": "52623bdc-fb65-4315-9c2b-d85856ccf54d",
                      "createdAt": "2025-07-15T11:54:42.141506Z",
                      "updatedAt": "2025-07-15T11:59:14.752380Z"
                  }
                  """)
          )),
      @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  ReadStatus with id {read-status-id} not found
                  """)
          ))
  })
  @RequestMapping(path = "/api/read-statuses/{read-status-id}", method = RequestMethod.PATCH)
  public ResponseEntity<ReadStatus> update(@PathVariable("read-status-id") UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request) {
    ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedReadStatus);
  }

  @Operation(summary = "User의 Message 읽음 상태 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  [
                      {
                          "userId": "59cdbeac-c530-47d8-905c-90fdaaf91884",
                          "channelId": "a5a4026a-f112-4398-8f88-a178a41001ce",
                          "lastReadAt": "-1000000000-01-01T00:00:00Z",
                          "id": "f61f950b-5f18-4e88-ae4a-6ac1955ede6b",
                          "createdAt": "2025-07-14T07:58:15.714150Z",
                          "updatedAt": null
                      },
                      {
                          "userId": "59cdbeac-c530-47d8-905c-90fdaaf91884",
                          "channelId": "1bd22115-057d-481d-8560-553e3b29f20a",
                          "lastReadAt": "-1000000000-01-01T00:00:00Z",
                          "id": "ff0dbff1-d02d-423b-ac77-928b9154edda",
                          "createdAt": "2025-07-14T08:55:33.829918Z",
                          "updatedAt": null
                      }
                  ]
                  """)
          ))
  })
  @RequestMapping(value = "/api/users/{user-id}/read-statuses", method = RequestMethod.GET)
  public ResponseEntity<List<ReadStatus>> findAllByUserId(@PathVariable("user-id") UUID userId) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatuses);
  }
}
