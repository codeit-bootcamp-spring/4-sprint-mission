package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

    private final UserDto userDto;
    private final User user;

    @JsonIgnore
    private final String password;   // 해시(BCrypt) 저장

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = user.getRole();
        if (role == null) return List.of();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscodeitUserDetails that = (DiscodeitUserDetails) o;

        // 1순위: 불변 고유 ID로 비교
        Object thisId = (this.user != null) ? this.user.getId() : null;
        Object thatId = (that.user != null) ? that.user.getId() : null;
        if (thisId != null && thatId != null) {
            return Objects.equals(thisId, thatId);
        }

        // 2순위(폴백): 이메일(로그인 ID)로 비교
        String thisEmail = (this.user != null) ? this.user.getEmail() : null;
        String thatEmail = (that.user != null) ? that.user.getEmail() : null;
        return Objects.equals(thisEmail, thatEmail);
    }

    @Override
    public int hashCode() {
        // equals 기준과 동일한 필드만 사용 (일관성 필수)
        Object id = (this.user != null) ? this.user.getId() : null;
        if (id != null) {
            return Objects.hash(id);
        }
        String email = (this.user != null) ? this.user.getEmail() : null;
        return Objects.hash(email);
    }
}
