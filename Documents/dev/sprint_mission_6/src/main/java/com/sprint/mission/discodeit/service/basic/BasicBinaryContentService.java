package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateDto dto) {
        BinaryContent binaryContent = binaryContentMapper.binaryContentCreateDtoToBinaryContent(dto);
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

        try {
            binaryContentStorage.put(savedBinaryContent.getId(), dto.getFile().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }
        return binaryContentMapper.binaryContentToBinaryContentDto(savedBinaryContent);
    }

    @Transactional(readOnly = true)
    @Override
    public BinaryContentDto find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 데이터가 존재하지 않습니다d"));
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
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 대상이 존재하지 않습니다."));
        binaryContentRepository.delete(binaryContent);
    }
}
