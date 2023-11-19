package com.shoppingcart.dto;

import com.shoppingcart.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private Long mobile;
    private String accessToken;
    private String refreshToken;
    private String role;
}
