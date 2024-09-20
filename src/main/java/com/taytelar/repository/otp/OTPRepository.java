package com.taytelar.repository.otp;

import com.taytelar.entity.otp.OTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTPRepository extends JpaRepository<OTPEntity,Long> {
    OTPEntity findByPhoneNumber(String phoneNumber);
}
