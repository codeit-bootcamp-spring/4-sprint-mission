package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentPostDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final MessageService messageService;

    @Override
    public BinaryContentResponseDto create(BinaryContentPostDto binaryContentPostDto) {
        BinaryContent binaryContent = binaryContentMapper.toBinaryContent(binaryContentPostDto);
        binaryContentRepository.save(binaryContent);
        return binaryContentMapper.toBinaryContentResponseDto(binaryContent);
    }

    @Override
    public BinaryContentResponseDto find(UUID id) {
        return binaryContentMapper.toBinaryContentResponseDto(binaryContentRepository.findById(id));
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids) {
        List<BinaryContentResponseDto> binaryContentResponseDtos = new ArrayList<>();
        binaryContentRepository.findAllById(ids).stream()
                .forEach(binaryContent ->
                        binaryContentResponseDtos.add(binaryContentMapper.toBinaryContentResponseDto(binaryContent)));
        return binaryContentResponseDtos;
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.delete(id);
    }

    // 테스트용 findAll()
    public List<BinaryContentResponseDto> findAll() {
        List<BinaryContentResponseDto> binaryContentResponseDtos = new ArrayList<>();
        binaryContentRepository.findAll().stream()
                .forEach(binaryContent ->
                        binaryContentResponseDtos.add(binaryContentMapper.toBinaryContentResponseDto(binaryContent)));
        return binaryContentResponseDtos;
    }
}
