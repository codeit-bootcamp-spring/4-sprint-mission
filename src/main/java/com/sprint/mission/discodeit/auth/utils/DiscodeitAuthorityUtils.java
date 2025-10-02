package com.sprint.mission.discodeit.auth.utils;

import com.sprint.mission.discodeit.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class DiscodeitAuthorityUtils {

    private final GrantedAuthority ADMIN_RULE = new SimpleGrantedAuthority("ROLE_ADMIN");
    private final GrantedAuthority CHANNEL_MANAGER_RULE = new SimpleGrantedAuthority("ROLE_CHANNEL_MANAGER");
    private final GrantedAuthority USER_ROLE = new SimpleGrantedAuthority("ROLE_USER");

    // DB 저장용
    public String createRole(String username) {
        return username.equals("admin") ? Role.ADMIN.name() : Role.USER.name();
    }

    // Spring 내에서 권한 부여
    public GrantedAuthority createAuthorityByUsername(String username) {
        return username.equals("admin") ? ADMIN_RULE : USER_ROLE;
    }

    public GrantedAuthority createAuthorityByRole(Role role) {
        // DB에서 가지고 온 role -> (접미사 ROLE_ 필요)
        return new SimpleGrantedAuthority("ROLE_" + role.name());
    }
}
