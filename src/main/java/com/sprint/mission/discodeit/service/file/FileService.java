package com.sprint.mission.discodeit.service.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    // File I/O
    public static void init(Path directory) {
        // 저장할 경로의 파일 초기화
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> void save(Path filePath, T data) {
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> load(Path directory, Class<T> type) {
        List<T> result = new ArrayList<>();

        if (!Files.exists(directory)) {
            return result;
        }

        try {
            Files.list(directory).forEach(path -> {
                try (
                        FileInputStream fis = new FileInputStream(path.toFile());
                        ObjectInputStream ois = new ObjectInputStream(fis)
                ) {
                    Object data = ois.readObject();

                    if (data instanceof List<?>) {
                        for (Object item : (List<?>) data) {
                            if (type.isInstance(item)) {
                                result.add(type.cast(item));
                            } else {
                                throw new RuntimeException("예상하지 않은 타입 발견: " + item.getClass());
                            }
                        }
                    } else {
                        if (type.isInstance(data)) {
                            result.add(type.cast(data));
                        } else {
                            throw new RuntimeException("예상하지 않은 타입 발견: " + data.getClass());
                        }
                    }

                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("역직렬화 실패: " + path.getFileName(), e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 순회 실패", e);
        }
        return result;
    }
}
