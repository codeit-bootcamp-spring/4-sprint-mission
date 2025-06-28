package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponseDto create(BinaryContentCreateDto binaryContentCreateDto) {
        BinaryContent createdBinaryContent = binaryContentMapper.binaryContentCreateDtoToBinaryContent(binaryContentCreateDto);
        binaryContentRepository.save(createdBinaryContent);
        return binaryContentMapper.binaryContentToBinaryContentResponseDto(createdBinaryContent);
    }

    @Override
    public BinaryContentResponseDto find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + id + " not found"));

        return binaryContentMapper.binaryContentToBinaryContentResponseDto(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids) {
        List<BinaryContentResponseDto> binaryContentResponseDtos = new ArrayList<>();
        for (UUID id : ids) {
            BinaryContent binaryContent = binaryContentRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + id + " not found"));
            binaryContentResponseDtos.add(binaryContentMapper.binaryContentToBinaryContentResponseDto(binaryContent));
        }
        return binaryContentResponseDtos;
    }

    @Override
    public void delete(UUID id) {
        if (binaryContentRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("BinaryContent with id " + id + " not found");
        }
        binaryContentRepository.delete(id);
    }
}
