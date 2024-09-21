package com.taytelar.repository.admin;

import com.taytelar.entity.admin.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, String> {
    AdminEntity findByEmailAddress(String emailAddress);
    AdminEntity findUserByPhoneNumber(String phoneNumber);
    Optional<AdminEntity> findByReferralCode(String referralCode);
}
