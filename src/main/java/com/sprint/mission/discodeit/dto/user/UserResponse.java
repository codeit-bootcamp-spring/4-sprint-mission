package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String username;
    private String email;
    private BinaryContentResponse profile;
    private boolean online;
    private Role role;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        assignBinaryContentResponseIfUserProfilePresent(user);
        this.role = user.getRole();
    }

    private void assignBinaryContentResponseIfUserProfilePresent(User user) {
        user.getOptionalProfile().ifPresent(binaryContent -> this.profile = new BinaryContentResponse(binaryContent));
    }

    public void isOnline(boolean online) {
        this.online = online;
    }
}
