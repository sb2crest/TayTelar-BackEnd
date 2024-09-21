package com.taytelar.service.service.user;

import com.taytelar.request.user.AuthRegisterRequest;
import com.taytelar.response.user.LoginResponse;

public interface AuthRegisterService {
    LoginResponse authRegisterAndLogin(AuthRegisterRequest request);
}
