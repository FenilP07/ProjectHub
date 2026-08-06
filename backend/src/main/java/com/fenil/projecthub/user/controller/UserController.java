package com.fenil.projecthub.user.controller;


import com.fenil.projecthub.user.dto.UserResponse;
import com.fenil.projecthub.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findById(
            @PathVariable UUID userId
    ) {
        UserResponse response = userService.findById(userId);

        return ResponseEntity.ok(response);
    }
}
