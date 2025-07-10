package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.RepositorySettings;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

  private final static String PATH = "user.dir";
  private final Path DIRECTORY;
  private final String EXTENSION;

  public FileBinaryContentRepository(RepositorySettings repositorySettings) {
    this.EXTENSION = repositorySettings.getEXTENSION();
    String fileDirectory = repositorySettings.getFILEDIRECTORY();
    this.DIRECTORY = Paths.get(System.getProperty(PATH),
        fileDirectory,
        "file-data-map",
        BinaryContent.class.getSimpleName());

    try {
      if (Files.notExists(DIRECTORY)) {
        Files.createDirectories(DIRECTORY);
      }
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 디렉토리 생성 실패", e);
    }
  }

  private Path resolvePath(UUID id) {
    return DIRECTORY.resolve(id + EXTENSION);
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    Path path = resolvePath(binaryContent.getId());
    try (
        FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
    ) {
      oos.writeObject(binaryContent);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return binaryContent;
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    BinaryContent binaryContentNullable = null;
    Path path = resolvePath(id);
    if (Files.exists(path)) {
      try (
          FileInputStream fis = new FileInputStream(path.toFile());
          ObjectInputStream ois = new ObjectInputStream(fis);
      ) {
        binaryContentNullable = (BinaryContent) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return Optional.ofNullable(binaryContentNullable);
  }

  @Override
  public List<BinaryContent> findAll() {
    try (Stream<Path> paths = Files.list(DIRECTORY)) {
      return paths
          .filter(p -> p.toString().endsWith(EXTENSION))
          .map(FileBinaryContentRepository::getBinaryContent)
          .toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
    try (Stream<Path> paths = Files.list(DIRECTORY)) {
      return paths
          .filter(p -> p.toString().endsWith(EXTENSION))
          .map(FileBinaryContentRepository::getBinaryContent)
          .filter(b -> binaryContentIds.contains(b.getId()))
          .toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean existsById(UUID id) {
    Path path = resolvePath(id);
    return Files.exists(path);
  }

  @Override
  public void deleteById(UUID id) {
    Path path = resolvePath(id);
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // 테스트에 사용
  @Override
  public void deleteAll() {
    try (Stream<Path> paths = Files.list(DIRECTORY)) {
      paths
          .filter(path -> path.toString().endsWith(EXTENSION))
          .forEach(path -> {
            try {
              Files.delete(path);
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          });
    } catch (IOException e) {
      throw new RuntimeException("Failed to list directory for deletion: " + DIRECTORY, e);
    }

  }

  private static BinaryContent getBinaryContent(Path p) {
    try (
        FileInputStream fis = new FileInputStream(p.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis);
    ) {
      return (BinaryContent) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
