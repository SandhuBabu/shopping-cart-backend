package com.shoppingcart.controller;


import com.shoppingcart.dto.AuthResponse;
import com.shoppingcart.entity.User;
import com.shoppingcart.exception.UserNotFoundException;
import com.shoppingcart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<AuthResponse> getUser(Principal principal) throws UserNotFoundException {
        String email = principal.getName();
        var user = userService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUserDetails(
            @RequestParam(value = "username",required = false) String username,
            @RequestParam(value = "mobile", required = false) Long mobile,
            Principal principal
    ) throws Exception {
        String userEmail = principal.getName();
        System.out.println(username);
        System.out.println(mobile);

        try{
            var res = userService.update(userEmail, username, mobile);
            return ResponseEntity.ok(res);
        } catch (UserNotFoundException e) {
            throw new Exception("Failed to update details");
        }
    }
}
