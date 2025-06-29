package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentResponseDto create(BinaryContentCreateDto binaryContentCreateDto) {
        BinaryContent binaryContent = binaryContentMapper.binaryContentCreateDtoToBinaryContent(binaryContentCreateDto);
        binaryContentRepository.save(binaryContent);

        return binaryContentMapper.binaryContentToResponseDto(binaryContent);
    }

    @Override
    public BinaryContentResponseDto find(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 ID 입니다."));

        return binaryContentMapper.binaryContentToResponseDto(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> binaryIds) {
        List<BinaryContent> binaryContents = binaryContentRepository.findAllById(binaryIds);
        return binaryContents.stream()
                .map(binaryContentMapper::binaryContentToResponseDto)
                .toList();
    }

    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new IllegalArgumentException("유효하지 않은 ID 입니다.");
        }

        binaryContentRepository.deleteById(binaryContentId);
    }
}
