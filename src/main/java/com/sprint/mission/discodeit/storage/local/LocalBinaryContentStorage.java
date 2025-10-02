package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path path;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String path) {
        this.path = Paths.get(path);
    }

    @PostConstruct
    private void init() {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        Path  filePath = resolvePath(binaryContentId);
        if(Files.exists(filePath)) {
            throw new RuntimeException("Binary content already exists");
        }
        try (
                OutputStream outputStream = Files.newOutputStream(filePath);
        ) {
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
         return binaryContentId;
    }

    public Path resolvePath(UUID binaryContentId) {
        return path.resolve(binaryContentId.toString());
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        Path filePath = resolvePath(binaryContentId);
        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentResponse response) {
        InputStream inputStream = get(response.getId());
        Resource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, response.getContentType())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(response.getSize()))
                .body(resource);
    }
}
