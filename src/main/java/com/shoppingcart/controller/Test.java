package com.shoppingcart.controller;

import com.shoppingcart.auth.JwtService;
import com.shoppingcart.exception.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/open")
@RequiredArgsConstructor
public class Test {

    private final JwtService jwtService;

    @GetMapping("/home")
    public String open() {
        return jwtService.generateAccessToken(1032L, "sandhu");
    }

    @GetMapping("/verify")
    public Object verify(@RequestParam String jws) throws TokenException {
        return jwtService.verifyToken(jws);
    }

}
