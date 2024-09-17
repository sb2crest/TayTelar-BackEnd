package com.taytelar.repository;

import com.taytelar.entity.affiliate.AffiliateUserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AffiliateUserRepository extends MongoRepository<AffiliateUserEntity, String> {
    AffiliateUserEntity findUserByPhoneNumber(String phoneNumber);

    AffiliateUserEntity findByReferralCode(String referralCode);
}
