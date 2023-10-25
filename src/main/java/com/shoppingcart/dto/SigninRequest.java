package com.shoppingcart.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SigninRequest {
    private String email;
    private String password;
}
