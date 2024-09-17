package com.taytelar.service.service.user;

import com.taytelar.request.user.LoginRequest;
import com.taytelar.request.user.UserRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.request.user.AddressRequest;
import com.taytelar.response.user.AddressResponse;
import com.taytelar.response.user.LoginResponse;
import com.taytelar.response.user.RegisterResponse;

import java.util.List;

public interface UserService {
    RegisterResponse register(UserRequest userRequest);

    LoginResponse login(LoginRequest loginRequest);

    SuccessResponse addAddress(AddressRequest addressRequest);

    List<AddressResponse> getAddresses(String userId);

    SuccessResponse updateAddress(AddressRequest addressRequest);

    SuccessResponse deleteAddress(String userId, Long addressId);
}
