//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.ReadStatus;
//import com.sprint.mission.discodeit.repository.ReadStatusRepository;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class JCFReadStatusRepository implements ReadStatusRepository {
//    private final Map<UUID, ReadStatus> data;
//
//    public JCFReadStatusRepository() { this.data = new HashMap<>(); }
//
//    @Override
//    public ReadStatus save(ReadStatus readStatus) {
//        this.data.put(readStatus.getId(), readStatus);
//        return readStatus;
//    }
//
//    @Override
//    public Optional<ReadStatus> findById(UUID id) {
//        return Optional.ofNullable(data.get(id));
//    }
//
//    @Override
//    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
//        return Optional.ofNullable(data.get(userId));
//    }
//
//    @Override
//    public List<ReadStatus> findAllByChannelId(UUID channelId) {
//        return data.values()
//                .stream()
//                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
//                .toList();
//    }
//
//    @Override
//    public List<ReadStatus> findAllByUserId(UUID userId) {
//        return data.values()
//                .stream()
//                .filter(readStatus -> readStatus.getUserId().equals(userId))
//                .toList();
//    }
//
//    @Override
//    public boolean existsById(UUID id) {
//        return data.containsKey(id);
//    }
//
//    @Override
//    public boolean existsByChannelIdAndUserId(UUID channelId, UUID userId) {
//        return data.containsKey(channelId) && data.get(channelId).getUserId().equals(userId);
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        data.remove(id);
//    }
//}
