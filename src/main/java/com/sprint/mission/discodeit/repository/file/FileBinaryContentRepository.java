package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.FileAccessException;
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
            // 트랜잭션시 롤백을 고려해서 RuntimeException을 상속받은 FileAccessException 형태로 예외 전환해서 던지도록 설정했습니다.
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        }
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        try {
            Map<UUID, BinaryContent> all = readFromFile();
            all.put(binaryContent.getId(), binaryContent);
            writeToFile(all);
            return binaryContent;
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public List<BinaryContent> saveAll(List<BinaryContent> binaryContents) {
        try {
            Map<UUID, BinaryContent> all = readFromFile();

            for (BinaryContent bc : binaryContents) {
                all.put(bc.getId(), bc);
            }
            writeToFile(all);
            return binaryContents;
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }


    @Override
    public Optional<BinaryContent> findById(UUID id) {
        try {
            return Optional.of(readFromFile().get(id));
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
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
        try {
            return new ArrayList<>(readFromFile().values());
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        try {
            return readFromFile().containsKey(id);
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Map<UUID, BinaryContent> all = readFromFile();
            if (all.remove(id) != null) {
                writeToFile(all);
            }
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
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
    private Map<UUID, BinaryContent> readFromFile() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(filePath))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (EOFException eof) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw e;
        }
    }
}
