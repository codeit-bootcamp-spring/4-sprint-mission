//package com.sprint.mission.discodeit.repository.file;
//
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import org.springframework.stereotype.Repository;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.*;
//
//@Repository
//public class FileUserRepository implements UserRepository {
//    private final Path DIRECTORY;
//    private final String EXTENSION = ".ser";
//    private final Map<String, User> users = new HashMap<>();
//
//    public FileUserRepository() {
//        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
//        if (Files.notExists(DIRECTORY)) {
//            try {
//                Files.createDirectories(DIRECTORY);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    private Path resolvePath(UUID id) {
//        return DIRECTORY.resolve(id + EXTENSION);
//    }
//
//    private Path resolvePath(String key) {
//        return DIRECTORY.resolve(key + EXTENSION);
//    }
//
//    @Override
//    public User save(User user) {
//        Path path = resolvePath(user.getId());
//        try (
//                FileOutputStream fos = new FileOutputStream(path.toFile());
//                ObjectOutputStream oos = new ObjectOutputStream(fos)
//        ) {
//            oos.writeObject(user);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return user;
//    }
//
//    @Override
//    public Optional<User> findById(UUID id) {
//        Path path = resolvePath(id);
//        if (!Files.exists(path)) return Optional.empty();
//
//        try (
//                FileInputStream fis = new FileInputStream(path.toFile());
//                ObjectInputStream ois = new ObjectInputStream(fis)
//        ) {
//            Object obj = ois.readObject();
//            if (obj instanceof User user) {
//                return Optional.of(user);
//            } else {
//                return Optional.empty(); // 타입 불일치 방어
//            }
//        } catch (IOException | ClassNotFoundException e) {
//            System.err.println("[findById] 역직렬화 실패: " + e.getMessage());
//            return Optional.empty(); // 예외 발생 시에도 빈 Optional 반환
//        }
////        User userNullable = null;
////        Path path = resolvePath(id);
////        if (Files.exists(path)) {
////            try (
////                    FileInputStream fis = new FileInputStream(path.toFile());
////                    ObjectInputStream ois = new ObjectInputStream(fis)
////            ) {
////                userNullable = (User) ois.readObject();
////            } catch (IOException | ClassNotFoundException e) {
////                throw new RuntimeException(e);
////            }
////        }
////        return Optional.ofNullable(userNullable);
//    }
//
//    @Override
//    public List<User> findAllById(List<UUID> ids) {
//        if (ids == null)
//            return List.of();
//        return ids.stream()
//                .map(this::findById)
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .toList();
//    }
//
//    @Override
//    public Optional<User> findByEmail(String email) {
//        if (users.isEmpty()) {
//            List<User> allUsers = findAll();
//            for (User user : allUsers) {
//                users.put(user.getEmail(), user);
//            }
//        }
////        User userNullable = null;
////        Path path = resolvePath(email);
////        if (Files.exists(path)) {
////            try (
////                    FileInputStream fis = new FileInputStream(path.toFile());
////                    ObjectInputStream ois = new ObjectInputStream(fis)
////            ) {
////                userNullable = (User) ois.readObject();
////            } catch (IOException | ClassNotFoundException e) {
////                throw new RuntimeException(e);
////            }
////        }
//        return Optional.ofNullable(users.get(email));
//    }
//
//    @Override
//    public List<User> findAll() {
//        try {
//            return Files.list(DIRECTORY)
//                    .filter(path -> path.toString().endsWith(EXTENSION))
//                    .map(path -> {
//                        try (
//                                FileInputStream fis = new FileInputStream(path.toFile());
//                                ObjectInputStream ois = new ObjectInputStream(fis)
//                        ) {
//                            return (User) ois.readObject();
//                        } catch (IOException | ClassNotFoundException e) {
//                            throw new RuntimeException(e);
//                        }
//                    })
//                    .toList();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public boolean existsById(UUID id) {
//        Path filePath = Paths.get(DIRECTORY.toString(), id.toString() + ".ser");
//        return Files.exists(filePath);
//    }
//
//    @Override
//    public boolean existsByUserName(String username) {
//        Path path = resolvePath(username);
//        return Files.exists(path);
//    }
//
//    @Override
//    public boolean existsByEmail(String email) {
//        Path path = resolvePath(email);
//        return Files.exists(path);
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        Path path = resolvePath(id);
//        if (Files.exists(path)) {
//            try {
//                Files.delete(path);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            System.out.println("삭제하려는 유저 상태 파일이 존재하지 않음: " + path);
//        }
//    }
//}
