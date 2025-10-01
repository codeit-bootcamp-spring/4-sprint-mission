package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {
  private final UserDto userDto;
  private final String password;

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return userDto.username();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DiscodeitUserDetails that = (DiscodeitUserDetails) o;
    return Objects.equals(this.userDto.id(), that.userDto.id());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.userDto.id());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    String roleName;
    switch (userDto.role()) {
      case ADMIN:
        roleName = "ROLE_ADMIN";
        break;
      case CHANNEL_MANAGER:
        roleName = "ROLE_CHANNEL_MANAGER";
        break;
      default:
        roleName = "ROLE_USER";
        break;
    }

    List<GrantedAuthority> list = new ArrayList<>();
    list.add(new SimpleGrantedAuthority(roleName));

    if (userDto.role() == Role.ADMIN) {
      list.add(new SimpleGrantedAuthority("user:read"));
      list.add(new SimpleGrantedAuthority("user:write"));
    }

    if (userDto.role() == Role.CHANNEL_MANAGER) {
      list.add(new SimpleGrantedAuthority("channel:read"));
      list.add(new SimpleGrantedAuthority("channel:write"));
    }

    if (userDto.role() == Role.USER) {
      list.add(new SimpleGrantedAuthority("user:read"));
    }

    return List.copyOf(list);
  }
}
