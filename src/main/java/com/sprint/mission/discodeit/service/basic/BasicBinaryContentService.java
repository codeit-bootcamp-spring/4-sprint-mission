package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentDto.create request) {
        MultipartFile file = Optional.ofNullable(request.getFile())
                .orElseThrow(() -> new NoSuchElementException("추가할 파일이 없습니다."));
        String fileName = Optional.ofNullable(file.getOriginalFilename())
                .orElse("defaultFileName");
        String contentType = file.getContentType();
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BinaryContent binaryContent = new BinaryContent(
                fileName,
                (long) bytes.length,
                contentType,
                bytes
        );
        return binaryContentRepository.save(binaryContent);
    }

    @Override
    public BinaryContent find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(BinaryContentDto.getBinaryContents request) {
        List<BinaryContent> binaryContents = Optional.ofNullable(binaryContentRepository.findAllByIdIn(request.getIds()))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new NoSuchElementException("BinaryContents with id " + request.getIds() + " not found"));

        List<UUID> foundIds = binaryContents.stream().map(BinaryContent::getId).toList();

        List<UUID> missingIds = request.getIds().stream()
                .filter(missingId -> !foundIds.contains(missingId))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new IllegalArgumentException("해당하는 바이너리 파일을 찾지 못하였습니다. missingIds: " + missingIds);
        }

        return binaryContents;
    }

    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }
}
