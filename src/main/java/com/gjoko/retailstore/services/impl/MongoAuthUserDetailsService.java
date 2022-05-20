package com.gjoko.retailstore.services.impl;

import com.gjoko.retailstore.persistence.dao.UserDto;
import com.gjoko.retailstore.services.IUsersService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class MongoAuthUserDetailsService implements UserDetailsService {

  private final IUsersService usersService;

  public MongoAuthUserDetailsService(IUsersService usersService) {
    this.usersService = usersService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<UserDto> userDtoOptional = usersService.getByUsername(username);
    if (userDtoOptional.isPresent()) {
      UserDto userDto = userDtoOptional.get();
      Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
      grantedAuthorities.add(new SimpleGrantedAuthority(userDto.getRole().toString()));
      return new User(userDto.getUsername(), userDto.getPassword(), grantedAuthorities);
    } else {
      throw new UsernameNotFoundException("No user exists with such username.");
    }
  }
}
