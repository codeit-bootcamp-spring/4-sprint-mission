package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {
    @Value(" ${discodeit.storage.local.root-path}")
    private Path root;

    private final String EXTENSION = ".ser";

    @PostConstruct
    void init() {
        if (Files.exists(root)) {
            File[] deleteList = root.toFile().listFiles();
            if (deleteList != null) {
                for (File file : deleteList) {
                    file.delete();
                }
            }
        } else {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    Path resolve(UUID binaryContentId) {
        return root.resolve(binaryContentId + EXTENSION);
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        Path path = resolve(binaryContentId);
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        InputStream inputStream = null;
        Path path = resolve(binaryContentId);
        if (Files.notExists(path)) {
            throw new BusinessLogicException(ErrorCode.BINARY_CONTENT_NOT_FOUND);
        } else {
            try {
                inputStream = Files.newInputStream(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return inputStream;
    }

    @Override
    public ResponseEntity download(BinaryContentDto binaryContentDto) throws IOException {
        Resource resource = new InputStreamResource(get(binaryContentDto.id()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + binaryContentDto.filename() + "\"")
                .contentType(MediaType.valueOf(binaryContentDto.contentType()))
                .body(resource);
    }
}
