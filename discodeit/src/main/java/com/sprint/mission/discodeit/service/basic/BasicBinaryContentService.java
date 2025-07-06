package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentResponseDto;
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
        BinaryContent content = binaryContentMapper.binaryContentCreateDtoToBinaryContent(dto);
        return binaryContentMapper.entityToDto(binaryContentRepository.save(content));
    }

    @Override
    public BinaryContentResponseDto find(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found : " + id));
        return binaryContentMapper.entityToDto(content);
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAll().stream()
                .filter(content -> ids.contains(content.getId()))
                .map(binaryContentMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("BinaryContent not found : " + id);
        }
        binaryContentRepository.deleteById(id);
    }
}
