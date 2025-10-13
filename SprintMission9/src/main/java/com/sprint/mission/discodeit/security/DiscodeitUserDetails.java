package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

  private final UserDto userDto;
  private final String password;
  private final DiscodeitAuthorityUtils authorityUtils;

  @Override
  public String getUsername() {
    return userDto.username();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<? extends GrantedAuthority> authorities = authorityUtils.createAuthorities(userDto);
    System.out.println("DEBUG: User " + userDto.username() + " authorities = " + authorities);
    return authorities != null ? authorities : AuthorityUtils.createAuthorityList();
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
    if (o instanceof DiscodeitUserDetails) {
      return this.userDto.equals(((DiscodeitUserDetails) o).userDto);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.userDto.hashCode();
  }

  public UUID getUserId() {
    return this.userDto.id();
  }
}
