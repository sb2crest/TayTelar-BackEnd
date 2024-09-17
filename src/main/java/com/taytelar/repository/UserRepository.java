package com.taytelar.repository;

import com.taytelar.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
    UserEntity findUserByUserId(String userId);

    UserEntity findUserByPhoneNumber(String phoneNumber);

    UserEntity findByReferralCode(String referralCode);
}
