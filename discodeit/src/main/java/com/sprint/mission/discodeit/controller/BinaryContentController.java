package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<BinaryContentResponseDto> findOne(@PathVariable("id") UUID id) {
        BinaryContentResponseDto result = binaryContentService.find(id);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/batch", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<BinaryContentResponseDto>> findAll(@RequestParam("ids") List<UUID> ids) {
        List<BinaryContentResponseDto> result = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(result);
    }
}
