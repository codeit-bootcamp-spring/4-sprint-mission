package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService, Serializable {

    private final Map<String, User> data;
    private static final FileUserService instance = new FileUserService();
    private static final String FILE_PATH = "study/users.txt";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String SERIALIZED_FILE_PATH = "study/users.ser";

    private FileUserService() {
        this.data = new HashMap<>();
    }

    public static FileUserService getInstance() {
        return instance;
    }

    //영속화를 위한 모든 채널을 찾는 메소드
    public Set<User> findAllUsers() {
        Set<User> users = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine();  //헤더 스킵

            while ((line = reader.readLine()) != null) {
                User user = User.fromCSV(line);

                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }

    //영속화를 위한 모든 채널을 저장하는 메소드
    public void saveAllUsers(List<User> users) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("username,email,phone,createdAt" + LINE_SEPARATOR);

            for (User user : users) {
                writer.write(user.toCSV() + LINE_SEPARATOR);
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //직렬화 저장 메소드
    public void saveSerializedUsers(List<User> users) {
        try (FileOutputStream fos = new FileOutputStream(SERIALIZED_FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //역직렬화 로드 메소드
    public List<User> loadSerializedUsers() {
        List<User> users = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(SERIALIZED_FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            users = (List<User>) ois.readObject();

            data.clear();
            for (User user : users) {
                data.put(user.getUserId(), user);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User createUser(User user) {
        List<User> users = this.getAllUsers();
        users.add(user);
        List<User> list = new ArrayList<>(users);

        saveAllUsers(list);

        saveSerializedUsers(list);

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        Map<String, User> resultMap = new HashMap<>();

        List<User> serializedUsers = loadSerializedUsers();
        if (serializedUsers != null) {
            for (User user : serializedUsers) {
                resultMap.put(user.getUserId(), user);
            }
        }

        Set<User> csvUsers = findAllUsers();
        for (User user : csvUsers) {
            resultMap.put(user.getUserId(), user);
        }

        data.clear();
        data.putAll(resultMap);

        return new ArrayList<>(resultMap.values());
    }

    @Override
    public User getUserById(String userId) {

        getAllUsers();

        return data.get(userId);
    }

    @Override
    public User updateUser(String userId, String newUsername, String newEmail, String newPhone) {

        User user = data.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        } else {
            user.setUsername(newUsername);
            user.setEmail(newEmail);
            user.setPhone(newPhone);
            user.setUpdatedAt(System.currentTimeMillis());

            ArrayList<User> updated = new ArrayList<>(data.values());

            saveAllUsers(updated);

            saveSerializedUsers(updated);

            return user;
        }
    }

    public User deleteUser(String userId) {
        User removed = data.remove(userId);
        if (removed != null) {

            List<User> userList = new ArrayList<>(data.values());
            saveAllUsers(userList);

            saveSerializedUsers(userList);
        }
        return removed;
    }

}