package com.taytelar.service.service.user;

import com.taytelar.request.user.UserRequest;
import com.taytelar.response.user.RegisterResponse;

public interface UserService {
    RegisterResponse register(UserRequest userRequest);

}
