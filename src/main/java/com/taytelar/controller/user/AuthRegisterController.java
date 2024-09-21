package com.taytelar.controller.user;

import com.taytelar.request.user.AuthRegisterRequest;
import com.taytelar.response.user.LoginResponse;
import com.taytelar.service.service.user.AuthRegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRegisterController {

    private final AuthRegisterService authRegisterService;

    @PostMapping("authRegisterAndLogin")
    public ResponseEntity<LoginResponse> authRegisterOrLogin(@Valid @RequestBody AuthRegisterRequest request) {
        LoginResponse response = authRegisterService.authRegisterAndLogin(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
