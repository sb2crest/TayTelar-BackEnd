package com.taytelar.service.serviceImplementation.user;

import com.taytelar.entity.user.UserEntity;
import com.taytelar.exception.user.UserDetailsMissMatchException;
import com.taytelar.exception.user.UserNotFoundException;
import com.taytelar.repository.UserRepository;
import com.taytelar.request.user.LoginRequest;
import com.taytelar.request.user.UserRequest;
import com.taytelar.response.user.RegisterResponse;
import com.taytelar.service.service.user.UserService;
import com.taytelar.util.Constants;
import com.taytelar.util.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final Generator generator;
    @Override
    public RegisterResponse register(UserRequest userRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(generator.generateId(Constants.USER_ID));
        userEntity.setFirstName(userRequest.getFirstName());
        userEntity.setLastName(userRequest.getLastName());
        userEntity.setEmailAddress(userRequest.getEmailAddress());
        userEntity.setPhoneNumber(userRequest.getPhoneNumber());
        userEntity.setPassword(userRequest.getPassword());
        userRepository.save(userEntity);

        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setUserId(userEntity.getUserId());
        registerResponse.setMessage(Constants.REGISTER_SUCCESS);

        return registerResponse;
    }

    @Override
    public RegisterResponse login(LoginRequest loginRequest) {
        UserEntity userEntity = userRepository.findUserByNumberOrEmail(loginRequest.getNumberOrEmail());
         if(userEntity == null){
             throw new UserNotFoundException(Constants.USER_NOT_FOUND);
         }

        boolean isEmailMatch = userEntity.getEmailAddress().equals(loginRequest.getNumberOrEmail());
        boolean isPhoneNumberMatch = userEntity.getPhoneNumber().equals(loginRequest.getNumberOrEmail());
        String password = loginRequest.getPassword();


        if((isEmailMatch  || isPhoneNumberMatch) && !userEntity.getPassword().equals(password)){
            throw new UserDetailsMissMatchException(Constants.USER_DETAILS_MISS_MATCH);
        }
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setUserId(userEntity.getUserId());
        registerResponse.setMessage(Constants.LOGIN_SUCCESS);

        return registerResponse;
    }
}
