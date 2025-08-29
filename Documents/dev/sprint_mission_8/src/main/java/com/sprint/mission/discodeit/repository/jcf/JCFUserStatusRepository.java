//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.UserStatus;
//import com.sprint.mission.discodeit.repository.UserStatusRepository;
//
//import java.util.*;
//
//public class JCFUserStatusRepository implements UserStatusRepository {
//    private final Map<UUID, UserStatus> data;
//
//    public JCFUserStatusRepository() {this.data = new HashMap<>();}
//
//    @Override
//    public UserStatus save(UserStatus userStatus) {
//        this.data.put(userStatus.getId(), userStatus);
//        return userStatus;
//    }
//
//    @Override
//    public Optional<UserStatus> findById(UUID id) {
//        return Optional.ofNullable(data.get(id));
//    }
//
//    @Override
//    public List<UserStatus> findAllByUserId(UUID userId) {
//        return this.data.values()
//                .stream()
//                .filter(userStatus -> userStatus.getUserId().equals(userId))
//                .toList();
//    }
//
//    @Override
//    public boolean existsByUserId(UUID userId) {
//        return this.data.containsKey(userId);
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        this.data.remove(id);
//    }
//}
