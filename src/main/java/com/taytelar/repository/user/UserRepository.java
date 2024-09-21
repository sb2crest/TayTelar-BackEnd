package com.taytelar.repository.user;

import com.taytelar.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
    UserEntity findUserByUserId(String userId);

    UserEntity findUserByPhoneNumber(String phoneNumber);

    Optional<UserEntity> findByReferralCode(String referralCode);

    UserEntity findByEmailAddress(String emailAddress);
}
