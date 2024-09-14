package com.taytelar.controller;

import com.taytelar.request.user.LoginRequest;
import com.taytelar.request.user.UserRequest;
import com.taytelar.response.user.LoginResponse;
import com.taytelar.response.user.RegisterResponse;
import com.taytelar.service.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * Endpoint to register a new user.
     *
     * @param userRequest The request object containing user registration details.
     * @return A ResponseEntity containing the RegisterResponse object and HTTP status 200 (OK) if registration is successful.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> userRegister(@Valid @RequestBody UserRequest userRequest){
        RegisterResponse registerResponse = userService.register(userRequest);
        return ResponseEntity.status(HttpStatus.OK).body(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }
}
