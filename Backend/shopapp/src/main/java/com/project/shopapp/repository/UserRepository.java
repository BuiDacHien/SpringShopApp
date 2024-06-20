package com.project.shopapp.repository;

import com.project.shopapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumberOrEmail OR u.email = :phoneNumberOrEmail")
    Optional<User> findByPhoneNumberOrEmail(@Param("phoneNumberOrEmail") String phoneNumberOrEmail);
}
