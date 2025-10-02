package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import java.util.Collection;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {
    private final UserResponse userResponse;
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("ROLE_".concat(userResponse.getRole().name()));
    }

    @Override
    public String getUsername() {
        return userResponse.getUsername();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiscodeitUserDetails that)) {
            return false;
        }

        return Objects.equals(userResponse, that.userResponse) && Objects.equals(password,
                that.password);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(userResponse);
        result = 31 * result + Objects.hashCode(password);
        return result;
    }
}
