package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class MultipartFileMapper {
    public BinaryContentCreateRequest toCreateRequest(MultipartFile file) {
        try {
            return new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + file.getOriginalFilename(), e);
        }
    }

    public List<BinaryContentCreateRequest> toCreateRequests(List<MultipartFile> files) {
        return files == null ? List.of() : files.stream()
                .map(this::toCreateRequest)
                .toList();
    }

    public Optional<BinaryContentCreateRequest> multiPartFileToDto(MultipartFile file) {

        try {
            BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
            return Optional.of(binaryContentCreateRequest);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
