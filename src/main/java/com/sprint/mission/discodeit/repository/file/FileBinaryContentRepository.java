package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(
        prefix="discodeit.repository",
        name="type",
        havingValue="file"
)
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path filePath;

    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory}/BinaryContent.ser") String filePathStr
    ) {
        this.filePath = Paths.get(filePathStr);
        try {
            Files.createDirectories(this.filePath.getParent());
            if (Files.notExists(this.filePath)) {
                writeToFile(new HashMap<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize binary content file", e);
        }
    }

    /**
     * 직렬화
     */
    private void writeToFile(Map<UUID, BinaryContent> map) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(filePath))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write binary content file", e);
        }
    }

    /**
     * 역직렬화
     */
    private Map<UUID, BinaryContent> readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(filePath))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (EOFException eof) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read binary content file", e);
        }
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Map<UUID, BinaryContent> all = readFromFile();
        all.put(binaryContent.getId(), binaryContent);
        writeToFile(all);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.of(readFromFile().get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        Set<UUID> idSet = new HashSet<>(ids);
        return findAll().stream()
                .filter(bc -> idSet.contains(bc.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(readFromFile().values());
    }

    @Override
    public boolean existsById(UUID id) {
        return readFromFile().containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, BinaryContent> all = readFromFile();
        if (all.remove(id) != null) {
            writeToFile(all);
        }
    }
}
