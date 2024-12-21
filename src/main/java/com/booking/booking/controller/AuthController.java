package com.booking.booking.controller;

import com.booking.booking.exception.UserAlreadyExistsException;
import com.booking.booking.model.User;
import com.booking.booking.request.LoginRequest;
import com.booking.booking.response.JwtResponse;
import com.booking.booking.security.jwt.JwtUtils;
import com.booking.booking.security.user.HotelUserDetails;
import com.booking.booking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    // 회원가입, 회원정보 수정 등 사용자 관련 비즈니스 로직 처리
    private final UserService userService;

    // 실제 로그인 인증 실행하는 매니저
    private final AuthenticationManager authenticationManager;

    // JWT 토큰 생성, 검증 처리
    private final JwtUtils jwtUtils;


    // 회원가입 API
    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            // 회원가입 처리
            userService.registerUser(user);
            return ResponseEntity.ok("Registration Succeeded!");
        }
        catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // 이메일과 비밀번호가 데이터베이스에 있는 사용자인지 확인하자 (AuthTokenFilter의 doFilterInternal 메소드 실행 -> 인증 객체 생성)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), // 입력한 이메일
                        loginRequest.getPassword() // 입력한 비밀번호
                )
        );

        /*
            authentication 객체 안에 들어있는 것
            1. Principal (UserDetails 타입으로 저장) - ID, 이메일, 비밀번호(암호화된 비밀번호), 권한 목록
            2. Credentials - 비밀번호(사용자가 입력한 원본 비밀번호)
            3. Authorities - 권한 정보
            4. Details - 추가 정보
         */

        // 확인된 사용자니까 시스템(SecurityContextHolder)에 등록해두자
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 등록했으니 JWT 토큰을 만들어주자
        String jwt = jwtUtils.generateJwtTokenForUser(authentication);

        // JWT 토큰에 넣을 사용자 정보(principal)를 가져오자
        // (HotelUserDetails)로 사용자 정보 형태 재변환 -> getPrincipal()이 Object 형태라서
        HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();


        // 이 사용자가 가진 권한 목록도 가져오자
        List<String> roles = userDetails.getAuthorities()
                                        .stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .toList();


        // 자, 이제 토큰이랑 사용자 정보를 클라이언트에게 보내주자
        return ResponseEntity.ok(new JwtResponse(
                userDetails.getId(), // 사용자 ID
                userDetails.getEmail(), // 이메일
                jwt, // JWT 토큰
                roles)); // 권한


    }
}
