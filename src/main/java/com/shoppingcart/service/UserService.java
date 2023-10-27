package com.shoppingcart.service;

import com.shoppingcart.dto.AuthResponse;
import com.shoppingcart.entity.User;
import com.shoppingcart.exception.UserNotFoundException;
import com.shoppingcart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public AuthResponse findUserByEmail(String email) throws UserNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("No user found"));
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();

    }

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }
}
