package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path}") String rootPath
    ) {
        this.root = Paths.get(rootPath);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize storage root: " + root, e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] content) {
        Path target = resolvePath(id);
        try {
            Files.write(target, content);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file [" + id + "] at " + target, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path target = resolvePath(id);
        try {
            return Files.newInputStream(target, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file [" + id + "] from " + target, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        InputStream in = get(dto.id());
        InputStreamResource resource = new InputStreamResource(in);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dto.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(dto.fileName(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .contentLength(dto.size())
                .body(resource);
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
