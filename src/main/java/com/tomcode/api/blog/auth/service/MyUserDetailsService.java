package com.tomcode.api.blog.auth.service;

import com.tomcode.api.blog.user.entity.User;
import com.tomcode.api.blog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

  private final UserRepository userDevRepo;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        userDevRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

    return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
        .password(user.getPassword())
        .authorities(user.getRole().name())
        .build();
  }
}
