package com.taytelar.repository;

import com.taytelar.entity.user.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity,Long> {
    List<AddressEntity> findByUserEntityUserId(String userId);

    AddressEntity findByUserEntityUserIdAndAddressId(String userId, Long addressId);
}
