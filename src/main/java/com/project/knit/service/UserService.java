package com.project.knit.service;

import com.project.knit.config.jwt.JwtTokenProvider;
import com.project.knit.domain.entity.Profile;
import com.project.knit.domain.entity.User;
import com.project.knit.domain.repository.ProfileRepository;
import com.project.knit.domain.repository.UserRepository;
import com.project.knit.dto.req.LoginReqDto;
import com.project.knit.dto.req.ProfileUpdateReqDto;
import com.project.knit.dto.res.CommonResponse;
import com.project.knit.dto.res.LoginResDto;
import com.project.knit.dto.res.ProfileResDto;
import com.project.knit.service.social.SocialOauth;
import com.project.knit.utils.StringUtils;
import com.project.knit.utils.enums.Role;
import com.project.knit.utils.enums.SocialLoginType;
import com.project.knit.utils.enums.StatusCodeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final List<SocialOauth> socialOauthList;
    private final HttpServletResponse response;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
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
    public CommonResponse<LoginResDto> login(LoginReqDto loginReqDto) {
        log.info("loginReqDto.getToken() : {}", loginReqDto.getToken());
        String snsToken = requestAccessToken(SocialLoginType.valueOf(loginReqDto.getType().toUpperCase()), loginReqDto.getToken());
        log.info("requested sns access token result : {}", snsToken);

        String accessToken = null;
        String refreshToken = null;
        User findUser = userRepository.findByEmail(loginReqDto.getEmail());
        String encodedPassword = createEncodedPassword(loginReqDto.getEmail(), loginReqDto.getPassword());
        LoginResDto resDto = new LoginResDto();
        if (findUser == null) {
            User user = User.builder()
                    .email(loginReqDto.getEmail())
                    .password(encodedPassword)
                    .type(loginReqDto.getType())
                    .token(loginReqDto.getToken())
                    .role(Role.USER.getKey())
                    .build();

            User createdUser = userRepository.save(user);

            Profile profile = Profile.builder()
                    .user(createdUser)
                    .email(createdUser.getEmail())
                    .nickname(StringUtils.getAdjNickname() + StringUtils.getNounNickname() + RandomStringUtils.randomNumeric(4))
                    .build();

            profileRepository.save(profile);

            accessToken = jwtTokenProvider.createAccessToken(createdUser.getEmail(), Collections.singletonList(createdUser.getRole()));
            refreshToken = jwtTokenProvider.createRefreshToken(RandomStringUtils.randomAlphanumeric(7));
            createdUser.addRefreshToken(refreshToken);
            resDto.setAccessToken(accessToken);
            resDto.setRefreshToken(refreshToken);
        } else {
            if (loginReqDto.getEmail().equals(findUser.getEmail()) && loginReqDto.getPassword().equals(findUser.getPassword())) {
                accessToken = jwtTokenProvider.createAccessToken(findUser.getEmail(), Collections.singletonList(findUser.getRole()));
                refreshToken = jwtTokenProvider.createRefreshToken(RandomStringUtils.randomAlphanumeric(7));
                findUser.addRefreshToken(refreshToken);
                resDto.setAccessToken(accessToken);
                resDto.setRefreshToken(refreshToken);
            }
        }

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "로그인 성공", resDto);
    }

    public CommonResponse<LoginResDto> refreshToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Access");
        String refreshToken = request.getHeader("Refresh");

        jwtTokenProvider.validateRefreshToken(refreshToken);

        String userEmailFromAccessToken = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userEmailFromAccessToken);

        if (user == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "사용자가 존재하지 않습니다.");
        }

        if (!user.getRefreshToken().equals(refreshToken)) {
            return CommonResponse.response(StatusCodeEnum.BAD_REQUEST.getStatus(), "Refresh Token의 유저 정보가 일치하지 않습니다.");
        }

        LoginResDto resDto = new LoginResDto();
        String newRefreshToken = jwtTokenProvider.createRefreshToken(RandomStringUtils.randomAlphanumeric(7));
        resDto.setAccessToken(jwtTokenProvider.createAccessToken(user.getId().toString(), Collections.singletonList(Role.USER.getKey())));
        resDto.setRefreshToken(newRefreshToken);
        user.addRefreshToken(newRefreshToken);

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "토큰 재발급 성공", resDto);
    }

    public CommonResponse<ProfileResDto> getProfileInfo(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.validateAccessToken(accessToken);
        String email = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(email);

        Profile profile = profileRepository.findByUser(user);

        ProfileResDto resDto = new ProfileResDto();
        resDto.setEmail(user.getEmail());
        resDto.setNickname(profile.getNickname());
        resDto.setGithub(profile.getGithub() == null ? null : profile.getGithub());
        resDto.setLinkedIn(profile.getLinkedIn() == null ? null : profile.getLinkedIn());
        resDto.setIntroduction(profile.getIntroduction() == null ? null : profile.getIntroduction());

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Profile Info Found", resDto);
    }

    @Transactional
    public CommonResponse<ProfileResDto> updateProfile(HttpServletRequest request, ProfileUpdateReqDto profileUpdateReqDto) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.validateAccessToken(accessToken);
        String email = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(email);

        Profile profile = profileRepository.findByUser(user);
        profile.updateProfile(
                profileUpdateReqDto.getNickname() == null ? profile.getNickname() : profileUpdateReqDto.getNickname(),
                profileUpdateReqDto.getGithub() == null ? profile.getGithub() : profileUpdateReqDto.getGithub(),
                profileUpdateReqDto.getLinkedIn() == null ? profile.getLinkedIn() : profileUpdateReqDto.getLinkedIn(),
                profileUpdateReqDto.getIntroduction() == null ? profile.getIntroduction() : profileUpdateReqDto.getIntroduction()
        );

        ProfileResDto resDto = new ProfileResDto();
        resDto.setEmail(user.getEmail());
        resDto.setNickname(profile.getNickname());
        resDto.setGithub(profile.getGithub() == null ? null : profile.getGithub());
        resDto.setLinkedIn(profile.getLinkedIn() == null ? null : profile.getLinkedIn());
        resDto.setIntroduction(profile.getIntroduction() == null ? null : profile.getIntroduction());

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Profile Updated", resDto);
    }

    // todo logout : token 삭제

    private String createEncodedPassword(String email, String password) {
        StringBuilder encodedPassword = new StringBuilder();
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), email.getBytes(StandardCharsets.UTF_8), 1024, 64 * 8);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] bytes = keyFactory.generateSecret(spec).getEncoded();
            for (final byte b : bytes) {
                encodedPassword.append(String.format("%02x", b & 0xff));
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return encodedPassword.toString();
    }
}
