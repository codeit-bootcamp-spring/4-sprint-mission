package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath);
        System.out.println("📦 LocalBinaryContentStorage bean 생성됨");
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(root);
    }

    public Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }

    @Override
    public UUID put (UUID id, byte[] bytes) throws IOException {
        Path path = resolvePath(id);
        Files.write(path, bytes);
        return id;
    }

    @Override
    public InputStream get(UUID id) throws IOException {
        Path path = resolvePath(id);
        return Files.newInputStream(path);
    }

    @Override
    public ResponseEntity<Resource> download (BinaryContentDto dto) throws IOException {
        Path path = resolvePath(dto.getId());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(dto.getContentType()))
                .body(resource);
    }
}
