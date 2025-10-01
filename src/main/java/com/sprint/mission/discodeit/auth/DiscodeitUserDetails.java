package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Role;
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
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return createAuthorities(userDto.role());
    }

    @Override
    public String getUsername() {
        return userDto.username();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DiscodeitUserDetails that)) return false;
        return Objects.equals(getUserDto().id(), that.getUserDto().id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUserDto().id());
    }

    public List<GrantedAuthority> createAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
