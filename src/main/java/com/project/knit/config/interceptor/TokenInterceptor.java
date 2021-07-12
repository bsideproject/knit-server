package com.project.knit.config.interceptor;

import com.project.knit.domain.entity.User;
import com.project.knit.domain.repository.UserRepository;
import com.project.knit.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;

    public TokenInterceptor(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String givenToken = jwtTokenProvider.resolveToken(request);
        log.info("given token : {}", givenToken);
        String email = jwtTokenProvider.getUserPk(givenToken);
        log.info("email : {}", email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다.");
        }

        log.info("user token : {}", user.getAccessToken());

        verifyToken(givenToken, user.getAccessToken());
        return true;
    }

    private void verifyToken(String givenToken, String userToken) {
        if (!givenToken.equals(userToken)) {
            throw new IllegalArgumentException("사용자 정보가 일치하지 않습니다.");
        }

        jwtTokenProvider.validateToken(givenToken);
    }
}
