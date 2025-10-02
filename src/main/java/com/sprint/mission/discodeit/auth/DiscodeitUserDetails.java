package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.auth.utils.DiscodeitAuthorityUtils;
import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {
    private final UserDto userDto;
    private final String password;
    private final DiscodeitAuthorityUtils authorityUtils;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(authorityUtils.createAuthorityByRole(userDto.role()));
    }

    @Override
    public String getUsername() {
        return this.userDto.username();
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
        if (!(o instanceof DiscodeitUserDetails that)) return false;
        return this.userDto.id().equals(that.userDto.id());
    }

    @Override
    public int hashCode() {
        return this.userDto.id().hashCode();
    }

    public UUID getId() {
        return this.userDto.id();
    }
}
