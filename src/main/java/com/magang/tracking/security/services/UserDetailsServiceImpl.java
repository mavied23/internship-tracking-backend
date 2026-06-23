package com.magang.tracking.security.services;

import com.magang.tracking.entity.User;
import com.magang.tracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Pastikan username tidak memiliki spasi di awal/akhir
    User user = userRepository.findByUsername(username.trim())
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));
    
    return UserDetailsImpl.build(user);
}
}