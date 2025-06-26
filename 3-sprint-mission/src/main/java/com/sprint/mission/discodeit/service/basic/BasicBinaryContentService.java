package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentResponseDto createBinaryContent(
            BinaryContentCreateDto binaryContentCreateDto) {
        BinaryContent binaryContent =
                binaryContentRepository.save(
                        binaryContentMapper.BinaryContentCreateDtoToBinaryContent(binaryContentCreateDto));
        return binaryContentMapper.BinaryContentToBinaryContentResponseDto(binaryContent);
    }

    @Override
    public BinaryContentResponseDto findBinaryContentById(UUID id) {
        BinaryContent binaryContent =
                binaryContentRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("BinaryContent를 찾을 수 없습니다."));

        return binaryContentMapper.BinaryContentToBinaryContentResponseDto(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllBinaryContentByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAll().stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .map(binaryContentMapper::BinaryContentToBinaryContentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBinaryContent(UUID id) {
        binaryContentRepository
                .findById(id)
                .ifPresentOrElse(
                        binaryContent -> {
                            binaryContentRepository.delete(id);
                        },
                        () -> {
                            throw new IllegalArgumentException("삭제할 binaryContent가 없습니다.");
                        });
    }
}
