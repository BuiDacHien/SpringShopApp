package com.project.shopapp.service;

import com.project.shopapp.entity.Token;
import com.project.shopapp.entity.User;

public interface TokenService {
    Token addToken(User user, String token, boolean isMobileDevice);

    Token refreshToken(String refreshToken, User user) throws Exception;
}
