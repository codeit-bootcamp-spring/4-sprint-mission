package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
@Component
public class FileUserRepository implements UserRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
        if(!Files.exists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            }catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) { // 여기에 resolve() 메서드를 넣는다
        return DIRECTORY.resolve(id + EXTENSION); 
    }

    @Override
    public User save(User user) {
        Path path = resolvePath(user.getId());
        try(
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
                ) {
                    oos.writeObject(user);
        }catch(IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Optional<User> findUser(UUID id) {

        User user = null;
        Path path = resolvePath(id);

        if(Files.exists(path)) { // 이 경로의 파일이 존재한다면,
            try(
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
                    ) {
                        user = (User)ois.readObject();
            } catch(IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path->path.toString().endsWith(EXTENSION))
                    .map(path-> {
                        try(
                            FileInputStream fis = new FileInputStream(path.toFile());
                            ObjectInputStream oos = new ObjectInputStream(fis)
                                ) {
                                    return (User) oos.readObject();
                        } catch(IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList(); // 처음 stream() 시작이 list였으니 마지막은 toList()가 맞아보인다
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsUser(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteUser(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
