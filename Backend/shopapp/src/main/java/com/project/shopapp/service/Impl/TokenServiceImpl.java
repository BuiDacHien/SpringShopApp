package com.project.shopapp.service.Impl;

import com.project.shopapp.components.JwtTokenUtil;
import com.project.shopapp.entity.Token;
import com.project.shopapp.entity.User;
import com.project.shopapp.exception.ExpiredTokenException;
import com.project.shopapp.exception.ResourceNotFoundException;
import com.project.shopapp.filters.JwtTokenFilter;
import com.project.shopapp.repository.TokenRepository;
import com.project.shopapp.service.TokenService;
import com.project.shopapp.utils.Constant;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private static final int MAX_TOKENS = 3;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    private final TokenRepository tokenRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    @Override
    public Token refreshToken(String refreshToken, User user) throws Exception{
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        if(existingToken == null) {
            throw new ResourceNotFoundException("Refresh token does not exist");
        }

        if(existingToken.getRefreshExpirationDate().isBefore(LocalDateTime.now())){
            tokenRepository.delete(existingToken);
            throw new ExpiredTokenException("Refresh token is expired");
        }

        String token = jwtTokenUtil.generateToken(user);
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);
        existingToken.setExpirationDate(expirationDateTime);
        existingToken.setToken(token);
        existingToken.setRefreshToken(UUID.randomUUID().toString());
        existingToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));

        return tokenRepository.save(existingToken);
    }

    @Transactional
    @Override
    public Token addToken(User user, String token, boolean isMobileDevice) {
        List<Token> userTokens = tokenRepository.findByUser(user);
        int tokenCount = userTokens.size();

        // Số lượng token vượt quá giới hạn, xóa một token cũ
        if (tokenCount >= MAX_TOKENS) {
            //Kiểm tra xem trong danh sách userTokens có tồn tại ít nhất
            //một token không phải là thiết bị di động (non-mobile)
            boolean hasNonMobileToken = !userTokens.stream().allMatch(Token::isMobile);
            Token tokenToDelete;
            if (hasNonMobileToken) {
                tokenToDelete = userTokens.stream()
                        .filter(userToken -> !userToken.isMobile())
                        .findFirst()
                        .orElse(userTokens.get(0));
            } else {
                // Nếu tất cả các token đều là mobile -> xóa token đầu trong danh sách.
                tokenToDelete = userTokens.get(0);
            }
            tokenRepository.delete(tokenToDelete);
        }
        long expirationInSeconds = expiration;
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expirationInSeconds);

        // Tạo mới một token cho người dùng
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .isRevoked(false)
                .expired(false)
                .tokenType(Constant.BEARER_TOKEN)
                .expirationDate(expirationDateTime)
                .isMobile(isMobileDevice)
                .build();

        // Add refresh token and date expire of refresh token
        newToken.setRefreshToken(UUID.randomUUID().toString());
        newToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));

        tokenRepository.save(newToken);
        return newToken;
    }
}
