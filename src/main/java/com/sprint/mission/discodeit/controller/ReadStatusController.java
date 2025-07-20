package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/*
 * 기존: /v1/read-statuses, PathVariable: read-status-id
 * 요구사항: /api/readStatuses, PathVariable: readStatusId
 * 요구사항에 맞춰 변경하였습니다.
 *
 * CommonResponse를 만들고 스웨거 문서에 반영도 했습니다.
 * 하지만 코드잇에서 주어진 API 스펙과 프론트엔드 코드와 맞지 않아 모두 제거했습니다.
 * 그래서 CommonResponse가 쓰인 곳은 없지만, CommonResponse를 비롯한 에러를 담은 응답 코드에 대한 피드백을 받으면 좋을 것 같아서 함께 올립니다.
 */
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {
    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusResponseDto> createReadStatus(@RequestBody @Valid ReadStatusCreateDto dto) {
        ReadStatusResponseDto created = readStatusService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<ReadStatusResponseDto>> getReadStatuses(@RequestParam(name = "userId") UUID userId) {
        return ResponseEntity.ok( readStatusService.findAllByUserId(userId));
    }

    @PatchMapping( "/{readStatusId}")
    public ResponseEntity<ReadStatusResponseDto> updateReadStatus(@PathVariable("readStatusId") UUID readStatusId,
                                                                  @RequestBody @Valid ReadStatusUpdateDto dto) {
        return ResponseEntity.ok(readStatusService.update(readStatusId, dto));
    }
}
