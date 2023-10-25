package com.shoppingcart.controller;


import com.shoppingcart.entity.User;
import com.shoppingcart.exception.UserNotFoundException;
import com.shoppingcart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public User getUser(Principal principal) throws UserNotFoundException {
        String email = principal.getName();
        var user = userService.findUserByEmail(email);
        return user;
    }
}
