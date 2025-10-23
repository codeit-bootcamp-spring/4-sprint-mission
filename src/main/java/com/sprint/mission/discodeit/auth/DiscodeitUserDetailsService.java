package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// - Telling Spring Security how to find and verify users from MY database (custom UserDetailsService)
//     instead of its temporary one (in memory)
// - If it's a bean it automatically replaces

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

    System.out.println("Attempting to load user by username: " + name);

    Optional<com.sprint.mission.discodeit.entity.User> optionalUser = userRepository.findByUsername(
        name);
    com.sprint.mission.discodeit.entity.User user = optionalUser.orElseThrow(
        UserNotFoundException::new);

    // converting the user's roles from the database to the format Spring Security needs
    Collection<? extends GrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()));
    System.out.println(
        "Found user: " + user.getUsername() + " with encoded password: " + user.getPassword());
    return new DiscodeitUserDetails(userMapper.toDto(user), user.getPassword(), authorities);


  }
}
