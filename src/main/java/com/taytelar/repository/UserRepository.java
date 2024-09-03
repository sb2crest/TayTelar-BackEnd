package com.taytelar.repository;

import com.taytelar.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface UserRepository extends JpaRepository<UserEntity,String> {
    @Query("select u from UserEntity u where u.userId = :userId")
    UserEntity findUserByUserId(String userId);

}
