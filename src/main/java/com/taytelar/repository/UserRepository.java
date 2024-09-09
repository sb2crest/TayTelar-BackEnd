package com.taytelar.repository;

import com.taytelar.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
    UserEntity findUserByUserId(String userId);

    @Query("SELECT u FROM UserEntity u WHERE u.emailAddress = :numberOrEmail OR u.phoneNumber = :numberOrEmail")
    UserEntity findUserByNumberOrEmail(String numberOrEmail);
}
