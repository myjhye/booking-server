package com.booking.booking.service;

import com.booking.booking.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtils jwtUtils;

    // 리프레시 토큰 저장
    public void saveRefreshToken(String userEmail, String refreshToken) {
        redisTemplate.opsForValue().set(
                userEmail,
                refreshToken,
                jwtUtils.getRefreshTokenExpirationTime(),
                TimeUnit.MILLISECONDS
        );
    }


    // 리프레시 토큰 검증
    public boolean validateRefreshToken(String userEmail, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get(userEmail);
        return refreshToken.equals(storedToken) && jwtUtils.validateToken(refreshToken);
    }


    // 리프레시 토큰 삭제 (로그아웃 시)
    public void deleteRefreshToken(String userEmail) {
        redisTemplate.delete(userEmail);
    }


    // 로그아웃
    public void logout(String userEmail, String accessToken) {

        // 1. 리프레시 토큰 삭제
        deleteRefreshToken(userEmail);

        // 2. 액세스 토큰을 블랙리스트에 추가
        // 남은 만료 시간 계산
        long remainingTime = jwtUtils.getRemainingTime(accessToken);

        // 블랙리스트에 등록
        redisTemplate.opsForValue()
                .set(
                    "BLACKLIST:" + accessToken,
                    "logout",
                    remainingTime,
                    TimeUnit.MILLISECONDS
                );
    }
}
