package com.project.shopapp.repository;

import com.project.shopapp.entity.Token;
import com.project.shopapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUser(User user);
    Token findByToken(String token);

    Token findByRefreshToken(String token);
}
