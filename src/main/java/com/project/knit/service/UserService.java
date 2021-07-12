package com.project.knit.service;

import com.project.knit.domain.entity.User;
import com.project.knit.domain.repository.UserRepository;
import com.project.knit.dto.req.LoginReqDto;
import com.project.knit.dto.res.CommonResponse;
import com.project.knit.jwt.JwtTokenProvider;
import com.project.knit.service.social.SocialOauth;
import com.project.knit.utils.enums.Role;
import com.project.knit.utils.enums.SocialLoginType;
import com.project.knit.utils.enums.StatusCodeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final List<SocialOauth> socialOauthList;
    private final HttpServletResponse response;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void request(SocialLoginType socialLoginType) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        String redirectURL = socialOauth.getOauthRedirectURL();
        try {
            response.sendRedirect(redirectURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String requestAccessToken(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        return socialOauth.requestAccessToken(code);
    }

    private SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 SocialLoginType 입니다."));
    }

    @Transactional
    public CommonResponse<String> login(LoginReqDto loginReqDto) {
        String snsToken = requestAccessToken(SocialLoginType.valueOf(loginReqDto.getType()), loginReqDto.getToken());
        log.info("{} sns token : {}", loginReqDto.getToken(), snsToken);

        String token = null;
        User findUser = userRepository.findByEmail(loginReqDto.getEmail());
        if (findUser == null) {
            User user = User.builder()
                    .email(loginReqDto.getEmail())
                    .password(loginReqDto.getPassword())
                    .type(loginReqDto.getType())
                    .token(loginReqDto.getToken())
                    .role(Role.USER.getKey())
                    .build();

            User createdUser = userRepository.save(user);

            token = jwtTokenProvider.createToken(createdUser.getEmail(), Collections.singletonList(createdUser.getRole()));

            createdUser.addAccessToken(token);
        } else {
            if (loginReqDto.getEmail().equals(findUser.getEmail()) && loginReqDto.getPassword().equals(findUser.getPassword())) {
                token = jwtTokenProvider.createToken(findUser.getEmail(), Collections.singletonList(findUser.getRole()));
                findUser.addAccessToken(token);
            }
        }

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "TOKEN", token);
    }
}
