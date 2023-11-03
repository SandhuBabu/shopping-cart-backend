package com.shoppingcart.service;


import com.shoppingcart.auth.JwtService;
import com.shoppingcart.dto.SigninRequest;
import com.shoppingcart.dto.SignupRequest;
import com.shoppingcart.dto.AuthResponse;
import com.shoppingcart.entity.RefreshToken;
import com.shoppingcart.entity.Role;
import com.shoppingcart.entity.User;
import com.shoppingcart.exception.TokenException;
import com.shoppingcart.exception.UserCreationException;
import com.shoppingcart.exception.UserNotFoundException;
import com.shoppingcart.repository.RefreshTokensRepository;
import com.shoppingcart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokensRepository tokensRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse createUser(SignupRequest signupRequest) throws Exception {
        try {
            var user = User.builder()
                    .role(Role.USER)
                    .fullName(signupRequest.getFullName())
                    .email(signupRequest.getEmail())
                    .password(passwordEncoder.encode(signupRequest.getPassword()))
                    .build();
            user = userRepository.save(user);

            var accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
            var refreshToken = RefreshToken.builder()
                    .userId(user.getId())
                    .refreshToken(jwtService.generateRefreshToken(user.getId(), user.getEmail()))
                    .build();

            tokensRepository.save(refreshToken);

            return AuthResponse.builder()
                    .id(user.getId())
                    .username(user.getFullName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getRefreshToken())
                    .build();

        } catch (DataIntegrityViolationException e) {
            throw new UserCreationException("Email already registered");
        } catch (Exception e) {
            throw new Exception("Can't create user");
        }
    }

    public AuthResponse signin(SigninRequest signinRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));

        var user = userRepository.findByEmail(signinRequest.getEmail());

        var accessToken = jwtService.generateAccessToken(user.get().getId(), user.get().getEmail());
        var refreshToken = RefreshToken.builder()
                .userId(user.get().getId())
                .refreshToken(jwtService.generateRefreshToken(user.get().getId(), user.get().getEmail()))
                .build();

        var savedToken = tokensRepository.findByUserId(user.get().getId());
        if(savedToken != null) {
            savedToken.setRefreshToken(refreshToken.getRefreshToken());
            tokensRepository.save(savedToken);
        } else {
            tokensRepository.save(new RefreshToken(user.get().getId(), refreshToken.getRefreshToken()));
        }

        return AuthResponse.builder()
                .id(user.get().getId())
                .username(user.get().getFullName())
                .role(user.get().getRole().name())
                .email(user.get().getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public String logout(String token) throws TokenException {
        try {
            var refreshToken = tokensRepository.findByRefreshToken(token);
            tokensRepository.delete(refreshToken);
            return  "Logout success";
        } catch (Exception e) {
            throw new TokenException("Can't complete logout");
        }
    }

    public AuthResponse refresh(String jwt) throws TokenException, UserNotFoundException {
        var savedToken = tokensRepository.findByRefreshToken(jwt);

        if(savedToken == null) {
            throw new TokenException("Invalid refresh token");
        }

        User user = userRepository.findById(savedToken.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found for this refresh token"));

        var accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        var refreshToken = RefreshToken.builder()
                        .refreshToken(jwtService.generateRefreshToken(user.getId(), user.getEmail()))
                                .build();
        savedToken.setRefreshToken(refreshToken.getRefreshToken());
        tokensRepository.save(savedToken);

        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
