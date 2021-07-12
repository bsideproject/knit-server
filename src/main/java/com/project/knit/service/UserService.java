package com.project.knit.service;

import com.project.knit.domain.entity.User;
import com.project.knit.domain.repository.UserRepository;
import com.project.knit.service.social.SocialOauth;
import com.project.knit.utils.enums.SocialLoginType;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final List<SocialOauth> socialOauthList;
    private final HttpServletResponse response;
    private final UserRepository userRepository;

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

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userRepository.findByEmail(userEmail);
    }
}
