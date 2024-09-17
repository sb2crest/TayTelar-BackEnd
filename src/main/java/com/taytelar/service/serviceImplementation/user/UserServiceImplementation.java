package com.taytelar.service.serviceImplementation.user;

import com.taytelar.entity.affiliate.AffiliateUserEntity;
import com.taytelar.entity.otp.OTPEntity;
import com.taytelar.entity.user.AddressEntity;
import com.taytelar.entity.user.UserEntity;
import com.taytelar.exception.otp.OtpNotFoundException;
import com.taytelar.exception.otp.UnknownUserTypeException;
import com.taytelar.exception.user.*;
import com.taytelar.repository.AddressRepository;
import com.taytelar.repository.AffiliateUserRepository;
import com.taytelar.repository.OTPRepository;
import com.taytelar.repository.UserRepository;
import com.taytelar.request.user.LoginRequest;
import com.taytelar.request.user.UserRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.request.user.AddressRequest;
import com.taytelar.response.user.AddressResponse;
import com.taytelar.response.user.LoginResponse;
import com.taytelar.response.user.RegisterResponse;
import com.taytelar.service.service.user.UserService;
import com.taytelar.util.Constants;
import com.taytelar.util.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    private final AffiliateUserRepository affiliateUserRepository;

    private final OTPRepository otpRepository;

    private final AddressRepository addressRepository;

    private final Generator generator;

    private final ModelMapper modelMapper;

    @Override
    public RegisterResponse register(UserRequest userRequest) {
        log.info("Register Request: {}", userRequest);
        String referredReferralCode = null;

        if (userRequest.getReferralCode() != null && !userRequest.getReferralCode().isEmpty()) {
            referredReferralCode = checkReferralCode(userRequest.getReferralCode());
            log.info("Referral Code: {}", referredReferralCode);
            if (referredReferralCode == null) {
                throw new UserDetailsMissMatchException(Constants.INVALID_REFERRAL_CODE);
            }
        }

        OTPEntity otpEntity = otpRepository.findByPhoneNumber(userRequest.getPhoneNumber());
        if (otpEntity == null) {
            throw new OtpNotFoundException(Constants.OTP_ENTITY_NOT_FOUND);
        }

        if (userRequest.getUserType().equalsIgnoreCase(Constants.CUSTOMER)) {
            UserEntity userEntity = userRepository.findUserByPhoneNumber(userRequest.getPhoneNumber());
            if (userEntity == null && otpEntity.isOtpVerified() && otpEntity.getPhoneNumber().equals(userRequest.getPhoneNumber())) {
                UserEntity user = new UserEntity();
                user.setUserId(generator.generateId(Constants.USER_ID));
                user.setFirstName(userRequest.getFirstName());
                user.setLastName(userRequest.getLastName());
                user.setUserType(userRequest.getUserType());
                user.setEmailAddress(userRequest.getEmailAddress());
                user.setEmailAddressVerified(false);
                user.setPhoneNumber(userRequest.getPhoneNumber());
                user.setPhoneNumberVerified(otpEntity.isOtpVerified());
                user.setReferralCode(generator.referralCode());
                user.setReferredReferralCode(referredReferralCode);
                user.setAuthenticationSource(generator.createAuthenticationSource(userRequest.getUserType()));
                user.setUserCreatedAt(LocalDateTime.now());
                userRepository.save(user);
                log.info("New Registration of customer user: {}", user);

                return createRegistrationResponse(user.getUserId(), user.getPhoneNumber());
            } else {
                log.info(Constants.USER_DETAILS_MISS_MATCH + Constants.OTP_NOT_VERIFIED);
                throw new UserDetailsMissMatchException(Constants.USER_DETAILS_MISS_MATCH + Constants.OTP_NOT_VERIFIED);
            }
        } else {
            AffiliateUserEntity affiliateUser = affiliateUserRepository.findUserByPhoneNumber(userRequest.getPhoneNumber());
            if (affiliateUser == null && otpEntity.isOtpVerified() && otpEntity.getPhoneNumber().equals(userRequest.getPhoneNumber())) {
                AffiliateUserEntity affiliateUserEntity = new AffiliateUserEntity();
                affiliateUserEntity.setAffiliateUserId(generator.generateId(Constants.AFFILIATE_USER_ID));
                affiliateUserEntity.setFirstName(userRequest.getFirstName());
                affiliateUserEntity.setLastName(userRequest.getLastName());
                affiliateUserEntity.setEmailAddress(userRequest.getEmailAddress());
                affiliateUserEntity.setEmailAddressVerified(false);
                affiliateUserEntity.setPhoneNumber(userRequest.getPhoneNumber());
                affiliateUserEntity.setPhoneNumberVerified(otpEntity.isOtpVerified());
                affiliateUserEntity.setReferralCode(generator.referralCode());
                affiliateUserEntity.setReferredReferralCode(referredReferralCode);
                affiliateUserEntity.setAuthenticationSource(generator.createAuthenticationSource(userRequest.getUserType()));
                affiliateUserEntity.setUserCreatedAt(LocalDateTime.now());
                affiliateUserRepository.save(affiliateUserEntity);
                log.info("New Registration of affiliate user: {}", affiliateUserEntity);

                return createRegistrationResponse(affiliateUserEntity.getAffiliateUserId(), affiliateUserEntity.getPhoneNumber());
            } else {
                log.info(Constants.USER_DETAILS_MISS_MATCH + Constants.OTP_NOT_VERIFIED);
                throw new UserDetailsMissMatchException(Constants.USER_DETAILS_MISS_MATCH + Constants.OTP_NOT_VERIFIED);
            }
        }
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login request: {}", loginRequest);
        LoginResponse loginResponse = new LoginResponse();
        if (loginRequest.getRequestType().equalsIgnoreCase(Constants.LOGIN) && loginRequest.getUserType().equalsIgnoreCase(Constants.CUSTOMER)) {
            UserEntity userEntity = userRepository.findUserByPhoneNumber(loginRequest.getPhoneNumber());
            if (userEntity != null && userEntity.isPhoneNumberVerified()) {
                loginResponse.setId(userEntity.getUserId());
                loginResponse.setFirstName(userEntity.getFirstName());
                loginResponse.setLastName(userEntity.getLastName());
                loginResponse.setEmailAddress(userEntity.getEmailAddress());
                loginResponse.setPhoneNumber(userEntity.getPhoneNumber());
                loginResponse.setReferralCode(userEntity.getReferralCode());
                loginResponse.setPhoneNumberVerified(userEntity.isPhoneNumberVerified());
            } else {
                log.info(Constants.USER_ACCOUNT_NOT_EXIST);
                throw new UserAccountNotExistException(Constants.USER_ACCOUNT_NOT_EXIST);
            }
        } else if (loginRequest.getRequestType().equalsIgnoreCase(Constants.LOGIN) && loginRequest.getUserType().equalsIgnoreCase(Constants.AFFILIATE)) {
            AffiliateUserEntity affiliateUser = affiliateUserRepository.findUserByPhoneNumber(loginRequest.getPhoneNumber());
            if (affiliateUser != null && affiliateUser.isPhoneNumberVerified()) {
                loginResponse.setId(affiliateUser.getAffiliateUserId());
                loginResponse.setFirstName(affiliateUser.getFirstName());
                loginResponse.setLastName(affiliateUser.getLastName());
                loginResponse.setEmailAddress(affiliateUser.getEmailAddress());
                loginResponse.setPhoneNumber(affiliateUser.getPhoneNumber());
                loginResponse.setReferralCode(affiliateUser.getReferralCode());
                loginResponse.setPhoneNumberVerified(affiliateUser.isPhoneNumberVerified());
            } else {
                log.info(Constants.AFFILIATE_USER_ACCOUNT_NOT_EXIST);
                throw new UserAccountNotExistException(Constants.AFFILIATE_USER_ACCOUNT_NOT_EXIST);
            }
        } else {
            log.info(Constants.UNKNOWN_USER_TYPE);
            throw new UnknownUserTypeException(Constants.UNKNOWN_USER_TYPE);
        }
        log.info("Login Response: {}", loginResponse);
        return loginResponse;
    }

    @Override
    public SuccessResponse addAddress(AddressRequest addressRequest) {
        UserEntity userEntity = userRepository.findUserByUserId(addressRequest.getUserId());
        validateUser(userEntity, addressRequest.getUserId());

        List<AddressEntity> addressEntities = addressRepository.findByUserEntityUserId(addressRequest.getUserId());
        if (checkAddressAlreadyExists(addressEntities, addressRequest)) {
            log.error("Exception while checking address : {}", Constants.ADDRESS_ALREADY_EXIST);
            throw new AddressAlreadyExistsException(Constants.ADDRESS_ALREADY_EXIST);
        }

        AddressEntity addressEntity = mapAddressRequestToEntity(new AddressEntity(),addressRequest, userEntity);
        addressRepository.save(addressEntity);
        log.info(Constants.ADDRESS_ADDED_SUCCESSFULLY + " : {}", addressEntity);

        return new SuccessResponse(Constants.ADDRESS_ADDED_SUCCESSFULLY, HttpStatus.OK.value());
    }

    @Override
    public List<AddressResponse> getAddresses(String userId) {
        UserEntity userEntity = userRepository.findUserByUserId(userId);
        validateUser(userEntity, userId);

        List<AddressEntity> addressEntities = addressRepository.findByUserEntityUserId(userId);
        log.info("List Of Addresses : {}", addressEntities);

        return addressEntities.stream()
                .map(address -> modelMapper.map(address, AddressResponse.class))
                .toList();
    }

    @Override
    public SuccessResponse updateAddress(AddressRequest addressRequest) {
        UserEntity userEntity = userRepository.findUserByUserId(addressRequest.getUserId());
        validateUser(userEntity, addressRequest.getUserId());

        AddressEntity addressEntity = addressRepository.findByUserEntityUserIdAndAddressId(addressRequest.getUserId(), addressRequest.getAddressId());
        validateAddress(addressEntity, addressRequest.getAddressId());

        AddressEntity entity = mapAddressRequestToEntity(addressEntity, addressRequest, userEntity);
        addressRepository.save(entity);
        log.info(Constants.ADDRESS_UPDATED_SUCCESSFULLY + " : {}", addressEntity);

        return new SuccessResponse(Constants.ADDRESS_UPDATED_SUCCESSFULLY, HttpStatus.OK.value());
    }


    @Override
    public SuccessResponse deleteAddress(String userId, Long addressId) {
        UserEntity userEntity = userRepository.findUserByUserId(userId);
        validateUser(userEntity, userId);

        AddressEntity addressEntity = addressRepository.findByUserEntityUserIdAndAddressId(userId, addressId);
        validateAddress(addressEntity, addressId);

        addressRepository.delete(addressEntity);
        log.info(Constants.ADDRESS_DELETED_SUCCESSFULLY + " : {}", addressEntity);

        return new SuccessResponse(Constants.ADDRESS_DELETED_SUCCESSFULLY, HttpStatus.OK.value());
    }

    private boolean checkAddressAlreadyExists(List<AddressEntity> addressEntities, AddressRequest addressRequest) {
        return addressEntities.stream()
                .anyMatch(address ->
                        address.getFirstName().equalsIgnoreCase(addressRequest.getFirstName()) &&
                                address.getLastName().equalsIgnoreCase(addressRequest.getLastName()) &&
                                address.getBuildingName().equalsIgnoreCase(addressRequest.getBuildingName()) &&
                                address.getStreetName().equalsIgnoreCase(addressRequest.getStreetName()) &&
                                address.getCityName().equalsIgnoreCase(addressRequest.getCityName()) &&
                                address.getStateName().equalsIgnoreCase(addressRequest.getStateName()) &&
                                address.getCountryName().equalsIgnoreCase(addressRequest.getCountryName()) &&
                                address.getPinCode().equalsIgnoreCase(addressRequest.getPinCode()) &&
                                address.getTypeOfAddress().equalsIgnoreCase(addressRequest.getTypeOfAddress()) &&
                                address.getLandMark().equalsIgnoreCase(addressRequest.getLandMark()));
    }

    private AddressEntity mapAddressRequestToEntity(AddressEntity addressEntity, AddressRequest addressRequest, UserEntity userEntity) {
        addressEntity.setFirstName(addressRequest.getFirstName());
        addressEntity.setLastName(addressRequest.getLastName());
        addressEntity.setBuildingName(addressRequest.getBuildingName());
        addressEntity.setStreetName(addressRequest.getStreetName());
        addressEntity.setCityName(addressRequest.getCityName());
        addressEntity.setStateName(addressRequest.getStateName());
        addressEntity.setCountryName(addressRequest.getCountryName());
        addressEntity.setPinCode(addressRequest.getPinCode());
        addressEntity.setTypeOfAddress(addressRequest.getTypeOfAddress());
        addressEntity.setLandMark(addressRequest.getLandMark());
        addressEntity.setUserEntity(userEntity);
        return addressEntity;
    }

    private String checkReferralCode(String referralCode) {
        UserEntity userEntity = userRepository.findByReferralCode(referralCode);
        if (userEntity != null) {
            return userEntity.getReferralCode();
        } else {
            AffiliateUserEntity affiliateUser = affiliateUserRepository.findByReferralCode(referralCode);
            if (affiliateUser != null) {
                return affiliateUser.getReferralCode();
            } else {
                return null;
            }
        }
    }

    private RegisterResponse createRegistrationResponse(String userId, String phoneNumber) {
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setId(userId);
        registerResponse.setPhoneNumber(phoneNumber);
        registerResponse.setMessage(Constants.REGISTER_SUCCESS);
        registerResponse.setStatusCode(HttpStatus.OK.value());
        log.info("Registration Response: {}", registerResponse);
        return registerResponse;
    }

    private void validateUser(UserEntity userEntity, String userId) {
        if (userEntity == null) {
            log.info("Exception : {} {}", Constants.USER_NOT_FOUND, userId);
            throw new UserNotFoundException(Constants.USER_NOT_FOUND);
        }
    }

    private void validateAddress(AddressEntity addressEntity, Long addressId) {
        if (addressEntity == null) {
            log.info("Exception : {} {}", Constants.ADDRESS_NOT_FOUND, addressId);
            throw new AddressNotFoundException(Constants.ADDRESS_NOT_FOUND);
        }
    }
}
