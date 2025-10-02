package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.message.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateDto dto) {
        log.info("첨부 파일 등록 요청");
        BinaryContent binaryContent = binaryContentMapper.binaryContentCreateDtoToBinaryContent(dto);
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

        try {
            binaryContentStorage.put(savedBinaryContent.getId(), dto.getFile().getBytes());
        } catch (IOException e) {
            log.error("첨부 파일 등록 실패-파일 저장 실패: binaryContentId = {]", savedBinaryContent.getId(), e);
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }
        log.info("첨부 파일 등록 성공: binaryContentId = {}", savedBinaryContent.getId());
        return binaryContentMapper.binaryContentToBinaryContentDto(savedBinaryContent);
    }

    @Transactional(readOnly = true)
    @Override
    public BinaryContentDto find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new BinaryContentNotFoundException(id));
        return binaryContentMapper.binaryContentToBinaryContentDto(binaryContent);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> allContent = binaryContentRepository.findAllById(ids);
        List<BinaryContent> selectedContent = allContent
                .stream()
                .filter(content -> ids.contains(content.getId()))
                .toList();

        return selectedContent.stream()
                .map(binaryContentMapper::binaryContentToBinaryContentDto)
                .toList();
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        log.info("첨부 파일 삭제 요청: binaryContentId = {}", id);
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("첨부 파일 삭제 실패-해당 첨부 파일 존재하지 않음: binaryContentId = {}", id);
                    return new BinaryContentNotFoundException(id);
                });
        binaryContentRepository.delete(binaryContent);
        log.info("첨부 파일 삭제 완료: binaryContentId = {}", id);
    }
}
