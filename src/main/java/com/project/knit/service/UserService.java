package com.project.knit.service;

import com.project.knit.config.jwt.JwtTokenProvider;
import com.project.knit.domain.entity.Content;
import com.project.knit.domain.entity.Profile;
import com.project.knit.domain.entity.Thread;
import com.project.knit.domain.entity.User;
import com.project.knit.domain.repository.ContentRepository;
import com.project.knit.domain.repository.ProfileRepository;
import com.project.knit.domain.repository.ThreadRepository;
import com.project.knit.domain.repository.UserRepository;
import com.project.knit.dto.req.LoginReqDto;
import com.project.knit.dto.req.ProfileUpdateReqDto;
import com.project.knit.dto.res.*;
import com.project.knit.service.social.SocialOauth;
import com.project.knit.utils.StringUtils;
import com.project.knit.utils.enums.*;
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
import java.util.ArrayList;
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
    private final ThreadRepository threadRepository;
    private final ContentRepository contentRepository;
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

    public CommonResponse<ThreadListResDto> getUserWaitingThreadList(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.validateAccessToken(accessToken);
        String email = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(email);

        List<Thread> threadList = threadRepository.findAllByUserAndStatusOrderByCreatedDateDesc(user, ThreadStatus.대기.name());
        List<ThreadResDto> resDtoList = new ArrayList<>();
        threadList.forEach(t -> {
            ThreadResDto res = new ThreadResDto();
            res.setId(t.getId());
            res.setTitle(t.getThreadTitle());
            res.setSubTitle(t.getThreadSubTitle());
            res.setThumbnailUrl(t.getThumbnailUrl());
            res.setCoverImage("https://knit-document.s3.ap-northeast-2.amazonaws.com/thread/cover/cover1.png");
            List<ContentResDto> contentList = new ArrayList<>();
            List<Content> contents = contentRepository.findAllByThreadIdOrderBySequence(t.getId());
            contents.forEach(c -> {
                ContentResDto contentRes = new ContentResDto();
                contentRes.setContentId(c.getId());
                contentRes.setType(c.getContentType());
                contentRes.setValue(c.getValue());
                contentRes.setSummary(c.getSummary());

                contentList.add(contentRes);
            });
            res.setContents(contentList);
            List<CategoryResDto> categoryList = new ArrayList<>();
            t.getCategories().forEach(c -> {
                CategoryResDto categoryRes = new CategoryResDto();
                categoryRes.setCategoryId(c.getId());
                categoryRes.setValue(c.getCategory());

                categoryList.add(categoryRes);
            });
            res.setCategories(categoryList);
            List<TagResDto> tagResList = new ArrayList<>();
            t.getTags().forEach(tr -> {
                TagResDto tagRes = new TagResDto();
                tagRes.setTagId(tr.getId());
                tagRes.setValue(tr.getTagName());

                tagResList.add(tagRes);
            });
            res.setTags(tagResList);
            List<ReferenceResDto> referenceList = new ArrayList<>();
            t.getReferences().forEach(r -> {
                ReferenceResDto referenceRes = new ReferenceResDto();
                referenceRes.setReferenceId(r.getId());
                referenceRes.setReferenceLink(r.getReferenceLink());
                referenceRes.setReferenceDescription(r.getReferenceDescription());

                referenceList.add(referenceRes);
            });
            res.setReferences(referenceList);
            res.setDate(t.getCreatedDate());
            res.setIsFeatured(t.getIsFeatured());

            resDtoList.add(res);
        });


        ThreadListResDto resDto = new ThreadListResDto();
        resDto.setCount(resDtoList.size());
        resDto.setThreads(resDtoList);

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "User 등재 전 대기문서 조회", resDto);
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
