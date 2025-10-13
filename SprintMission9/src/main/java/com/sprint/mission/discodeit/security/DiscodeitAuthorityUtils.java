package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class DiscodeitAuthorityUtils {

  private final List<GrantedAuthority> ADMIN_ROLES =
      AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_CHANNEL_MANAGER", "ROLE_USER");
  private final List<GrantedAuthority> CHANNEL_MANAGER_ROLES =
      AuthorityUtils.createAuthorityList("ROLE_CHANNEL_MANAGER", "ROLE_USER");
  private final List<GrantedAuthority> USER_ROLES = AuthorityUtils.createAuthorityList("ROLE_USER");

  public List<GrantedAuthority> createAuthorities(UserDto userDto) {
    return switch (userDto.role()) {
      case ADMIN -> AuthorityUtils.createAuthorityList(
          "ROLE_ADMIN", "ROLE_CHANNEL_MANAGER", "ROLE_USER");
      case CHANNEL_MANAGER -> AuthorityUtils.createAuthorityList(
          "ROLE_CHANNEL_MANAGER", "ROLE_USER");
      default -> AuthorityUtils.createAuthorityList("ROLE_USER");
    };
  }
}
