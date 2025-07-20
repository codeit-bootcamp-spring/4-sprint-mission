package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentResponseDto create(BinaryContentCreateDto dto) {
        BinaryContent binaryContent = binaryContentMapper.binaryContentCreateDtoToBinaryContent(dto);
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
        return binaryContentMapper.binaryContentToBinaryContentResponseDto(savedBinaryContent);
    }

    @Override
    public BinaryContentResponseDto find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 데이터가 존재하지 않습니다d"));
        return binaryContentMapper.binaryContentToBinaryContentResponseDto(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> allContent = binaryContentRepository.findAll();

        List<BinaryContent> selectedContent = allContent
                .stream()
                .filter(content -> ids.contains(content.getId()))
                .toList();

        return selectedContent.stream()
                .map(binaryContentMapper::binaryContentToBinaryContentResponseDto)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 대상이 존재하지 않습니다."));
        binaryContentRepository.deleteById(id);
    }
}
