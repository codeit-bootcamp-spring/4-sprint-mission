package com.sprint.mission.discodeit.repository.fixture;

import com.sprint.mission.discodeit.entity.User;

public class UserFixture {
    public static User createUserHong() {
        return new User("hong", "hong@example.com", "hong1234", null);
    }

    public static User createUserHong_DuplicateEmail() {
        return new User("hong2", "hong@example.com", "hong1234", null);
    }

    public static User createUserKim() {
        return new User("kim", "kim@example.org", "kim1234", null);
    }

    public static User createUserLee() {
        return new User("lee", "lee@example.com", "lee1234", null);
    }
}
