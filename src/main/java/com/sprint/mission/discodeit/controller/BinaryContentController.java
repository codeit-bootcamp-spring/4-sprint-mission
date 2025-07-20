package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/*
 * 기존: /v1/binary-contents, PathVariable: binary-content-id
 * 요구사항: /api/binaryContents, PathVariable: binaryContentId
 * 요구사항에 맞춰 변경하였습니다.
 *
 * CommonResponse를 만들고 스웨거 문서에 반영도 했습니다.
 * 하지만 코드잇에서 주어진 API 스펙과 프론트엔드 코드와 맞지 않아 모두 제거했습니다.
 * 그래서 CommonResponse가 쓰인 곳은 없지만, CommonResponse를 비롯한 에러를 담은 응답 코드에 대한 피드백을 받으면 좋을 것 같아서 함께 올립니다.
 */
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {
    private final BinaryContentService binaryContentService;

    // 단일 조회
    @GetMapping( "/{binaryContentId}")
    public ResponseEntity<BinaryContentResponseDto> getBinaryContent(@PathVariable("binaryContentId") UUID binaryContentId) {
        return ResponseEntity.ok(
                binaryContentService.find(binaryContentId)
        );
    }

    // 여러 개 조회
    @GetMapping
    public ResponseEntity<List<BinaryContentResponseDto>> getBinaryContents(@RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        return ResponseEntity.ok(
                binaryContentService.findAllByIdIn(binaryContentIds)
        );
    }
}
