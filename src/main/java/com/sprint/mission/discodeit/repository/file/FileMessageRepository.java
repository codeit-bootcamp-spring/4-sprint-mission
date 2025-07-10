package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.RepositorySettings;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
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
public class FileMessageRepository implements MessageRepository {

  private final static String PATH = "user.dir";
  private final Path DIRECTORY;
  private final String EXTENSION;

  public FileMessageRepository(RepositorySettings repositorySettings) {
    this.EXTENSION = repositorySettings.getEXTENSION();
    String fileDirectory = repositorySettings.getFILEDIRECTORY();

    this.DIRECTORY = Paths.get(System.getProperty(PATH),
        fileDirectory,
        "file-data-map",
        Message.class.getSimpleName());

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
  public Message save(Message message) {
    Path path = resolvePath(message.getId());
    try (
        FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return message;
  }

  @Override
  public Optional<Message> findById(UUID id) {
    Path path = resolvePath(id);
    if (Files.exists(path)) {
      try (
          FileInputStream fis = new FileInputStream(path.toFile());
          ObjectInputStream ois = new ObjectInputStream(fis)
      ) {
        Message message = (Message) ois.readObject();
        return Optional.of(message);
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    throw new RuntimeException("File does not exist");
  }

  @Override
  public List<Message> findAll() {
    try (Stream<Path> paths = Files.list(DIRECTORY)) {
      return paths
          .filter(path -> path.toString().endsWith(EXTENSION))
          // convert each object in path
          .map(FileMessageRepository::getMessage)
          .toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public List<Message> findByChannelId(UUID channelId) {
    try (Stream<Path> paths = Files.list(DIRECTORY)) {
      return paths
          .filter(path -> path.toString().endsWith(EXTENSION))
          .map(path -> {
            try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
              return (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          })
          .filter(message -> message.getChannelId().equals(channelId))
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

  @Override
  public void deleteAll() {
    try {
      Files.list(DIRECTORY)
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

  private static Message getMessage(Path path) {
    try (
        FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis)
    ) {
      return (Message) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
