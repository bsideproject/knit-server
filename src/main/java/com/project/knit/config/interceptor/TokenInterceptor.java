package com.project.knit.config.interceptor;

import com.project.knit.config.jwt.JwtTokenProvider;
import com.project.knit.domain.entity.User;
import com.project.knit.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

//    public TokenInterceptor(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
//        this.userRepository = userRepository;
//        this.jwtTokenProvider = jwtTokenProvider;
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        log.info("[TEMP] getPathInfo() : {}", request.getPathInfo());
        log.info("[TEMP] getMethod() : {}", request.getMethod());

        String givenAccessToken = jwtTokenProvider.resolveToken(request);
        if (givenAccessToken != null) {
            log.info("given token : {}", givenAccessToken);
            String email = jwtTokenProvider.getUserPk(givenAccessToken);
            log.info("email : {}", email);
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다.");
            }
            log.info("user refresh token : {}", user.getRefreshToken());
            verifyToken(givenAccessToken);
        }
        return true;
    }

    private void verifyToken(String givenToken) {
        jwtTokenProvider.validateAccessToken(givenToken);
    }
}
