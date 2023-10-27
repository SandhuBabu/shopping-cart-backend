package com.shoppingcart.auth;

import com.shoppingcart.dto.SigninRequest;
import com.shoppingcart.dto.SignupRequest;
import com.shoppingcart.dto.AuthResponse;
import com.shoppingcart.exception.TokenException;
import com.shoppingcart.exception.UserNotFoundException;
import com.shoppingcart.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest signupRequest) throws Exception {
        AuthResponse res = authService.createUser(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody SigninRequest signinRequest) throws Exception {
        AuthResponse response = authService.signin(signinRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Principal principal) throws TokenException {

        if(principal == null) {
            throw new TokenException("User not found, logout failed");
        }

        String email = principal.getName();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(authService.logout(email));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) throws TokenException, UserNotFoundException {
        var res = authService.refresh(refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
