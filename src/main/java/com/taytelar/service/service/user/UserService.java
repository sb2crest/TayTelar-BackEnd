package com.taytelar.service.service.user;

import com.taytelar.request.user.LoginRequest;
import com.taytelar.request.user.UserRequest;
import com.taytelar.response.user.LoginResponse;
import com.taytelar.response.user.RegisterResponse;

public interface UserService {
    RegisterResponse register(UserRequest userRequest);

    LoginResponse login(LoginRequest loginRequest);
}
