package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.Message;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "메세지 관련 API")
public class MessageController {

    private final MessageService messageService;
    private final BinaryContentMapper binaryContentMapper;

    //postman에서는 작동하는데 swagger에서는 json으로 넣은 dto를 인식을 못합니다...
    @Operation(summary = "메세지 생성", description = "신규 메세지를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "메세지 생성 완료")
    @ApiResponse(responseCode = "400", description = "JSON 형식이 잘못되었거나 필수 파라미터가 누락되었습니다")
    @ApiResponse(responseCode = "415", description = "잘못된 타입의 데이터입니다")
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Message> createMessage(
//            @Parameter(
//                    description = "메세지 생성 json",
//                    required = true,
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON_VALUE,
//                            schema = @Schema(implementation = MessageCreateRequest.class)
//                    )
//            )
            @Parameter(
                    description = "메세지 정보 json",
                    required = true,
                    schema =@Schema(type = "string", format = "binary"))
            @RequestPart("request") MessageCreateRequest request,
            @Parameter(description = "첨부파일 리스트")
            @RequestPart(value = "file", required = false) List<MultipartFile> file
    ) {
        List<BinaryContentCreateRequest> attachments = binaryContentMapper.addBinaryContentCreateRequest(file);
        com.sprint.mission.discodeit.entity.Message message = messageService.create(request, attachments);
        Message messageDto = messageService.makeDto(message);
        return new ResponseEntity<>(messageDto, HttpStatus.CREATED);
    }

    @Operation(summary = "메세지 수정", description = "기존 메세지를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "메세지 수정 완료")
    @ApiResponse(responseCode = "400", description = "JSON 형식이 잘못되었거나 필수 파라미터가 누락되었습니다")
    @ApiResponse(responseCode = "404", description = "해당 메세지를 찾을 수 없습니다")
    @RequestMapping(value = "/{messageId}",method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> patchMessage(
            @Parameter(description = "메세지 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable("messageId") UUID messageId,
            @Parameter(
                    description = "메세지 수정 json",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageUpdateRequest.class)
                    )
            )
            @RequestBody MessageUpdateRequest request) {
        com.sprint.mission.discodeit.entity.Message message = messageService.update(messageId,request);
        Message messageDto = messageService.makeDto(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{messageId}",method = RequestMethod.DELETE)
    @ApiResponse(responseCode = "204", description = "메세지 삭제 완료")
    @ApiResponse(responseCode = "400", description = "잘못된 UUID 형식 입니다")
    @ApiResponse(responseCode = "404", description = "해당 메세지를 찾을 수 없습니다")
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/channel",method = RequestMethod.GET)
    @Operation(summary = "채널 내 모든 메세지 조회", description = "채널 안에서 작성된 모든 메세지를 조회합니다")
    @ApiResponse(responseCode = "200", description = "메세지 리스트 조회 성공")
    public ResponseEntity<List<Message>> getMessageByChannelId(
            @Parameter(description = "채널 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @RequestParam("channelId") UUID channelId) {
        List<com.sprint.mission.discodeit.entity.Message> foundMessages = messageService.findAllByChannelId(channelId);
        List<Message> messageDtos = new ArrayList<>();
        for (com.sprint.mission.discodeit.entity.Message message : foundMessages) {
            messageDtos.add(messageService.makeDto(message));
        }
        return new ResponseEntity<>(messageDtos, HttpStatus.OK);
    }


}
