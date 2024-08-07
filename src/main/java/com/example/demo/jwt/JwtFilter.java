package com.example.demo.jwt;

import com.example.demo.dto.CustomUserDetails;
import com.example.demo.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.sound.midi.Soundbank;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //requet에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        //토큰이 null인지 검증
        if(authorization == null || !authorization.startsWith("Bearer ")){
            System.out.println("token null");
            filterChain.doFilter(request, response);

            return;
        }

        //Bearer 제거 후 토큰 검증
        String token = authorization.split(" ")[1];

        //토큰 소별 시간 검증
        if(jwtUtil.isExpired(token)){
            System.out.println("token expired");
            filterChain.doFilter(request, response);

            return;
        }

        //토큰에서 username이랑 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password("tempassword")
                .role(role)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        //세션 생성
        SecurityContextHolder.getContext().setAuthentication(authToken);

        //다음 필터로 넘김
        filterChain.doFilter(request, response);
    }
}
