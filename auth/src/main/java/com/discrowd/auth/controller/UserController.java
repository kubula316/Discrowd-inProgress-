package com.discrowd.auth.controller;

import com.discrowd.auth.model.AuthenticationResponse;
import com.discrowd.auth.model.RegisterRequest;
import com.discrowd.auth.model.dto.UserData;
import com.discrowd.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/data")
    public UserData register(@RequestParam Long id) {
        return userService.getUserData(id);
    }
}
