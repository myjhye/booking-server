package com.booking.booking.security.jwt;

import com.booking.booking.security.user.HotelUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

// JWT 토큰을 만들고 검증하는 도구 (도구 기능 제공만 하고 실제 처리는 다른 파일에서)
@Component
public class JwtUtils {

    // 에러 로그를 남길 도구
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // application.properties에서 설정한 비밀키
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    // application.properties에서 설정한 토큰 만료 시간(ms)
    @Value("${app.jwt-expiration-milliseconds}")
    private int jwtExpirationTime;

    // 로그인 성공한 사용자 정보로 JWT 토큰 만들어줘
    public String generateJwtTokenForUser(Authentication authentication) {

        // 로그인한 사용자 정보 가져와서
        HotelUserDetails userPrincipal = (HotelUserDetails) authentication.getPrincipal();

        // 사용자의 권한 정보도 가져와서
        List<String> roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // JWT 토큰을 만들어서 반환해줘
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // 사용자 이메일 넣고
                .claim("roles", roles) // 권한 정보 넣고
                .setIssuedAt(new Date()) // 발행 시간 넣고
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationTime)) // 만료 시간 넣고
                .signWith(key(), SignatureAlgorithm.HS256).compact(); // 비밀키로 서명해서 완성
    }


    // 비밀키로 토큰을 암호화/복호화할 수 있는 키를 만들어줘 --> jwtSecret를 실제로 암호화에 사용할 수 있는 형태로 변환
    private Key key() {
        // 1. BASE64로 인코딩된 문자열을 디코딩
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        // 2. 디코딩된 바이트 배열로 실제 암호화 키 생성
        return Keys.hmacShaKeyFor(keyBytes);
    }


    // JWT 토큰에서 사용자 이메일(subject)을 꺼내줘
    public String getUserNameFromToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key())
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }


    //  JWT 토큰이 유효한지 검사해줘
    public boolean validateToken(String token) {
        try {
            // 토큰을 해석해봐서 문제가 없으면 true 반환
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        }
        catch (MalformedJwtException e) {
            // 토큰 형식이 잘못 됐어
            logger.error("Invalid jwt token: {} ", e.getMessage());
        }
        catch (ExpiredJwtException e) {
            // 토큰이 만료 됐어
            logger.error("Expired token: {} ", e.getMessage());
        }
        catch (UnsupportedJwtException e) {
            // 지원하지 않는 토큰이야
            logger.error("This token is not supported: {} ", e.getMessage());
        }
        catch (IllegalArgumentException e) {
            // 토큰이 비어있어
            logger.error("NO claims found: {} ", e.getMessage());
        }

        return false;
    }
}
