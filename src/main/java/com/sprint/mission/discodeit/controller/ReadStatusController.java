package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ReadStatus;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatus")
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "수신 정보 관련 API")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "신규 수신 정보 생성", description = "신규 수신 정보를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "수신 정보 생성 완료")
    @ApiResponse(responseCode = "400", description = "JSON 형식이 잘못되었거나 필수 파라미터가 누락되었습니다")
    public ResponseEntity<ReadStatus> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        com.sprint.mission.discodeit.entity.ReadStatus readStatus = readStatusService.create(request);
        ReadStatus readStatusDto = readStatusService.makeDto(readStatus);
        return new ResponseEntity<>(readStatusDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    @Operation(summary = "기존 수신 정보 수정", description = "ReadStatus 정보를 일부 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수신 정보 수정 완료")
    @ApiResponse(responseCode = "400", description = "JSON 형식이 잘못되었거나 필수 파라미터가 누락되었습니다")
    @ApiResponse(responseCode = "404", description = "해당 수신 정보를 찾을 수 없습니다")
    public ResponseEntity<ReadStatus> patchReadStatus(
            @Parameter(description = "수정할 ReadStatus의 UUID")
            @PathVariable("readStatusId") UUID readStatusId, @RequestBody ReadStatusUpdateRequest request) {
        com.sprint.mission.discodeit.entity.ReadStatus readStatus = readStatusService.update(readStatusId, request);
        ReadStatus readStatusDto = readStatusService.makeDto(readStatus);
        return new ResponseEntity<>(readStatusDto, HttpStatus.OK);
    }

    @RequestMapping( value = "/user", method = RequestMethod.GET)
    @Operation(summary = "유저의 모든 수신 정보 조회", description = "유저의 모든 수신 정보를 리스트로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "모든 수신 정보 조회 완료")
    @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다")
    public ResponseEntity<List<ReadStatus>> getReadStatusByUserId(
            @Parameter(description = "수신 정보를 조회할 유저의 UUID")
            @RequestParam ("userId") UUID userId) {
        List<com.sprint.mission.discodeit.entity.ReadStatus> readStatusList = readStatusService.findAllByUserId(userId);
        List<ReadStatus> readStatusDtos = new ArrayList<>();
        for (com.sprint.mission.discodeit.entity.ReadStatus readStatus : readStatusList) {
            readStatusDtos.add(readStatusService.makeDto(readStatus));
        }
        return new ResponseEntity<>(readStatusDtos, HttpStatus.OK);
    }
}
