package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
public class FileChannelRepository implements ChannelRepository {

    private final Path Directory;
    private final String Extension = ".ser";

    public FileChannelRepository() {
        this.Directory = Paths.get(System.getProperty("user.dir"),"file-data-map",Channel.class.getSimpleName()); // ?
        if(!Files.exists(Directory)) {
            try {
                Files.createDirectories(Directory);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return Directory.resolve(id + Extension);
    }

    @Override
    public Channel save(Channel channel) { // outStream을 쓴다. channel에 저장함

        Path path = resolvePath(channel.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(channel);

        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        return channel;
    }

    @Override
    public Optional<Channel> findId(UUID id) { // 아이디 찾기
        Channel channelNullable = null;
        Path path = resolvePath(id);
        if(Files.exists(path)) {
            try(FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)) {
                channelNullable = (Channel)ois.readObject();
            }catch(IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(channelNullable);
    }

    @Override
    public List<Channel> findAll() {
        try{
            return Files.list(Directory) // ???
                    .filter(path->path.toString().endsWith(Extension))
                    .map(path-> { // map()을 써서 path로 변환했다?
                        try(
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel)ois.readObject();
                        } catch(IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList(); // 이걸 리스트로 만듬
        }catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsId(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteId(UUID id) {

        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

    }
}

