package com.taytelar.repository;

import com.taytelar.entity.cart.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends MongoRepository<CartEntity, String> {
    CartEntity findByUserId(String userId);
}
