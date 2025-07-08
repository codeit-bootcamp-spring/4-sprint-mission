package com.sprint.mission.discodeit.dto.request;

public class UserRequest {
    public static class Create {
        String userName;
        String password;
        String email;
    }

    public static class Update {
        String newUserName;
        String newPassword;
        String newEmail;
    }
}
