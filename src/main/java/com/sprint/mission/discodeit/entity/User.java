package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force=true)// 클래스에 필수 필드가 포함되어 있다. NoArgsContructor를 강제 적용해야한다.
public class User implements Serializable {
    // Annotated member is not a part of the serialization mechanism
    // 주석이 달린 멤버는 직렬화 메커니즘의 일부가 아닙니다. 이거 implements 안넣어서 그런거임
    @Serial
    private static final long serialVersionUID = 1L; // 직렬화

    private final UUID id;
    private String nickName;
    private String email;
    private String password;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(String nickName, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.nickName = nickName;
        this.email = email;
        this.password = password;
    }

    public void updateNickName(String newNickname) {
        nickName = newNickname;
        this.updatedAt = Instant.now();
    }

    public String toCSV() {
        return id + "," + nickName + "," + password + "," + createdAt + "," + updatedAt;
    }
}
