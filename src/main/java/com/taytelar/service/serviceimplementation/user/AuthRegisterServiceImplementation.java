package com.taytelar.service.serviceimplementation.user;

import com.taytelar.entity.admin.AdminEntity;
import com.taytelar.entity.affiliate.AffiliateUserEntity;
import com.taytelar.entity.user.UserEntity;
import com.taytelar.exception.otp.UnknownUserTypeException;
import com.taytelar.repository.admin.AdminRepository;
import com.taytelar.repository.user.AffiliateUserRepository;
import com.taytelar.repository.user.UserRepository;
import com.taytelar.request.user.AuthRegisterRequest;
import com.taytelar.response.user.LoginResponse;
import com.taytelar.service.service.user.AuthRegisterService;
import com.taytelar.util.Constants;
import com.taytelar.util.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthRegisterServiceImplementation implements AuthRegisterService {

    private final UserRepository userRepository;

    private final AffiliateUserRepository affiliateUserRepository;

    private final AdminRepository adminRepository;

    private final Generator generator;


    @Override
    public LoginResponse authRegisterAndLogin(AuthRegisterRequest request) {
        log.info("AuthRegister Request: {}", request);

        return switch (request.getUserType()) {
            case Constants.CUSTOMER -> handleCustomerRegisterAndLogin(request);
            case Constants.AFFILIATE -> handleAffiliateRegisterAndLogin(request);
            case Constants.ADMIN -> handleAdminRegisterAndLogin(request);
            default -> throw new UnknownUserTypeException(Constants.UNKNOWN_USER_TYPE + ": " + request.getUserType());
        };
    }

    private LoginResponse handleCustomerRegisterAndLogin(AuthRegisterRequest request) {
        UserEntity userEntity = userRepository.findByEmailAddress(request.getEmailAddress());
        if (userEntity == null) {
            userEntity = new UserEntity();
            userEntity.setUserId(generator.generateId(Constants.USER_ID));
            userEntity.setFirstName(request.getFirstName());
            userEntity.setLastName(request.getLastName());
            if((request.getEmailAddress() == null && request.getEmailAddress().isEmpty())){
                userEntity.setEmailAddress(request.getEmailAddress());
                userEntity.setEmailAddressVerified(true);
            }
            userEntity.setEmailAddressVerified(false);
            userEntity.setPhoneNumberVerified(false);
            userEntity.setUserType(request.getUserType());
            userEntity.setUserCreatedAt(LocalDateTime.now());
            userEntity.setAuthenticationSource(generator.createAuthenticationSource(request.getAuthType()));
            userEntity.setReferralCode(generator.referralCode());
            userRepository.save(userEntity);
            log.info("User Entity : {}", userEntity);
        }
        return authResponse(userEntity);
    }


    private LoginResponse handleAffiliateRegisterAndLogin(AuthRegisterRequest request) {
        AffiliateUserEntity affiliateUser = affiliateUserRepository.findByEmailAddress(request.getEmailAddress());
        if (affiliateUser == null) {
            affiliateUser = new AffiliateUserEntity();
            affiliateUser.setAffiliateUserId(generator.generateId(Constants.AFFILIATE_USER_ID));
            affiliateUser.setFirstName(request.getFirstName());
            affiliateUser.setLastName(request.getLastName());
            if((request.getEmailAddress() != null && !request.getEmailAddress().isEmpty())){
                affiliateUser.setEmailAddress(request.getEmailAddress());
                affiliateUser.setEmailAddressVerified(true);
            }
            affiliateUser.setEmailAddressVerified(false);
            affiliateUser.setPhoneNumberVerified(false);
            affiliateUser.setUserType(request.getUserType());
            affiliateUser.setUserCreatedAt(LocalDateTime.now());
            affiliateUser.setAuthenticationSource(generator.createAuthenticationSource(request.getAuthType()));
            affiliateUser.setReferralCode(generator.referralCode());
            affiliateUserRepository.save(affiliateUser);
            log.info("Affiliate User Entity : {}", affiliateUser);
        }
        return authResponse(affiliateUser);
    }

    private LoginResponse handleAdminRegisterAndLogin(AuthRegisterRequest request) {
        AdminEntity adminEntity = adminRepository.findByEmailAddress(request.getEmailAddress());
        if (adminEntity == null) {
            adminEntity = new AdminEntity();
            adminEntity.setAdminId(generator.generateId(Constants.ADMIN_ID));
            adminEntity.setFirstName(request.getFirstName());
            adminEntity.setLastName(request.getLastName());
            if((request.getEmailAddress() != null && !request.getEmailAddress().isEmpty())){
                adminEntity.setEmailAddress(request.getEmailAddress());
                adminEntity.setEmailAddressVerified(true);
            }
            adminEntity.setEmailAddressVerified(false);
            adminEntity.setPhoneNumberVerified(false);
            adminEntity.setUserType(request.getUserType());
            adminEntity.setUserCreatedAt(LocalDateTime.now());
            adminEntity.setAuthenticationSource(generator.createAuthenticationSource(request.getAuthType()));
            adminEntity.setReferralCode(generator.referralCode());
            adminRepository.save(adminEntity);
            log.info("Admin Entity : {}", adminEntity);
        }

        return authResponse(adminEntity);
    }

    private <T> LoginResponse authResponse(T entity) {
        LoginResponse response = new LoginResponse();

        if (entity instanceof UserEntity userEntity) {
            response.setId(userEntity.getUserId());
            response.setFirstName(userEntity.getFirstName());
            response.setLastName(userEntity.getLastName());
            response.setEmailAddress(userEntity.getEmailAddress());
            response.setEmailAddressVerified(userEntity.isEmailAddressVerified());
            response.setPhoneNumber(userEntity.getPhoneNumber());
            response.setPhoneNumberVerified(userEntity.isPhoneNumberVerified());
            response.setReferralCode(userEntity.getReferralCode());
        } else if (entity instanceof AffiliateUserEntity affiliateUserEntity) {
            response.setId(affiliateUserEntity.getAffiliateUserId());
            response.setFirstName(affiliateUserEntity.getFirstName());
            response.setLastName(affiliateUserEntity.getLastName());
            response.setEmailAddress(affiliateUserEntity.getEmailAddress());
            response.setEmailAddressVerified(affiliateUserEntity.isEmailAddressVerified());
            response.setPhoneNumber(affiliateUserEntity.getPhoneNumber());
            response.setPhoneNumberVerified(affiliateUserEntity.isPhoneNumberVerified());
            response.setReferralCode(affiliateUserEntity.getReferralCode());
        } else if (entity instanceof AdminEntity adminEntity) {
            response.setId(adminEntity.getAdminId());
            response.setFirstName(adminEntity.getFirstName());
            response.setLastName(adminEntity.getLastName());
            response.setEmailAddress(adminEntity.getEmailAddress());
            response.setEmailAddressVerified(adminEntity.isEmailAddressVerified());
            response.setPhoneNumber(adminEntity.getPhoneNumber());
            response.setPhoneNumberVerified(adminEntity.isPhoneNumberVerified());
            response.setReferralCode(adminEntity.getReferralCode());
        }
        return response;
    }


}



