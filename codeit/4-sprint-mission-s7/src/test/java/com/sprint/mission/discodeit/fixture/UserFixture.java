package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.User;

public class UserFixture {
    public static User createUserKim() {
        return new User("kim", "kim@nate.com", "kim1234", null);
    }

    public static User createUserJim() {
        return new User("jim", "jim@nate.com", "jim1234", null);
    }

    public static User createUserJane() {
        return new User("jane","jane@nate.com", "jane1234", null);
    }

    public static User createUserJane_DuplicateEmail() {
        return new User("jades","jane@nate.com", "jane1234", null);
    }
}
