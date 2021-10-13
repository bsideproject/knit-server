package com.project.knit.service;

import com.project.knit.config.jwt.JwtTokenProvider;
import com.project.knit.domain.entity.Thread;
import com.project.knit.domain.entity.*;
import com.project.knit.domain.repository.*;
import com.project.knit.dto.req.ProfileUpdateReqDto;
import com.project.knit.dto.res.*;
import com.project.knit.service.social.SocialOauth;
import com.project.knit.utils.StringUtils;
import com.project.knit.utils.enums.Role;
import com.project.knit.utils.enums.SocialLoginType;
import com.project.knit.utils.enums.StatusCodeEnum;
import com.project.knit.utils.enums.ThreadStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    private final ThreadContributorRepository threadContributorRepository;
    private final ExpiredTokenRepository expiredTokenRepository;
    private final UserTokenRepository userTokenRepository;
    private final ThreadLikeRepository threadLikeRepository;

    private final RestTemplate restTemplate = new RestTemplate();

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
    public CommonResponse<LoginResDto> login(HttpServletRequest request, String type) {
        String snsToken = request.getHeader("token");
        log.info("sns access token : {}", snsToken);

        String email = "";
        if (type.equalsIgnoreCase("NAVER")) {
            email = getNaverUserProfile(snsToken);
        }

        if (type.equalsIgnoreCase("GOOGLE")) {
            email = getGoogleUserProfile(snsToken);
        }

        String accessToken;
        String refreshToken;
        User findUser = userRepository.findByEmail(email);
        LoginResDto resDto = new LoginResDto();
        if (findUser == null) {
            User user = User.builder()
                    .email(email)
                    .type(type.toUpperCase())
                    .token(snsToken)
                    .role(Role.USER.getKey())
                    .build();

            User createdUser = userRepository.save(user);
            log.info(StringUtils.getAdjNickname());
            log.info(StringUtils.getNounNickname());
            log.info(RandomStringUtils.randomNumeric(4));

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

            UserToken userToken = UserToken.builder().userId(user.getId()).refreshToken(refreshToken).build();
            userTokenRepository.save(userToken);
            userRepository.save(createdUser);
        } else {
            if (email.equals(findUser.getEmail())) {
                accessToken = jwtTokenProvider.createAccessToken(findUser.getEmail(), Collections.singletonList(findUser.getRole()));
                refreshToken = jwtTokenProvider.createRefreshToken(RandomStringUtils.randomAlphanumeric(7));
                findUser.addRefreshToken(refreshToken);

                resDto.setAccessToken(accessToken);
                resDto.setRefreshToken(refreshToken);

                UserToken userToken = UserToken.builder().userId(findUser.getId()).refreshToken(refreshToken).build();
                userTokenRepository.save(userToken);
                userRepository.save(findUser);
            }
        }

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "로그인 성공", resDto);
    }

    @Transactional
    public <T> CommonResponse<T> logout(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.validateAccessToken(accessToken);
        String email = jwtTokenProvider.getUserPk(accessToken);
        var user = userRepository.findByEmail(email);

        if (user == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "User Not Found.");
        }

        List<UserToken> userTokenList = userTokenRepository.findAllByUserId(user.getId());
        if (!userTokenList.isEmpty()) {
            userTokenList.forEach(ut -> {
                var expiredToken = ExpiredToken.builder()
                        .accessToken(jwtTokenProvider.resolveToken(request))
                        .refreshToken(ut.getRefreshToken())
                        .build();

                expiredTokenRepository.save(expiredToken);
            });
        }
        userTokenRepository.deleteAll(userTokenList);

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "로그아웃 성공");
    }

    private String getGoogleUserProfile(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> userInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<GoogleEmailResDto> responseEntity = restTemplate.postForEntity("https://openidconnect.googleapis.com/v1/userinfo", userInfoRequest, GoogleEmailResDto.class);
        String email = Objects.requireNonNull(responseEntity.getBody()).getEmail();
        log.info("response : {}", email);

        return email;
    }

    private String getNaverUserProfile(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> userInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<NaverEmailResDto> responseEntity = restTemplate.postForEntity("https://openapi.naver.com/v1/nid/me", userInfoRequest, NaverEmailResDto.class);
        String email = Objects.requireNonNull(responseEntity.getBody().getResponse()).getEmail();

        return email;
    }

    public CommonResponse<LoginResDto> refreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh");
        var expiredTokenList = expiredTokenRepository.findAllByRefreshToken(refreshToken);

        if (!jwtTokenProvider.validateRefreshToken(refreshToken) || !expiredTokenList.isEmpty()) {
            throw new IllegalArgumentException("Refresh Token Expired. You need to re-login.");
        }

        List<UserToken> userTokenList = userTokenRepository.findAllByRefreshToken(refreshToken);
        if (userTokenList == null || userTokenList.isEmpty()) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "사용자가 존재하지 않습니다.");
        }

        User user = userRepository.findById(userTokenList.get(0).getUserId()).orElse(null);
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

        if (user == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "User Not Found.");
        }

        Profile profile = profileRepository.findByUser(user);

        if (profile == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "Profile Not Found.");
        }

        ProfileResDto resDto = new ProfileResDto();
        resDto.setEmail(user.getEmail());
        resDto.setNickname(profile.getNickname());
        resDto.setGithub(profile.getGithub() == null ? null : profile.getGithub());
        resDto.setLinkedIn(profile.getLinkedIn() == null ? null : profile.getLinkedIn());
        resDto.setIntroduction(profile.getIntroduction() == null ? null : profile.getIntroduction());

        Long userId = user.getId();
        List<ThreadContributor> contributeList = threadContributorRepository.findAllByContributorUserId(user.getId());
        List<ThreadContributeResDto> contributeResDtoList = new ArrayList<>();
        contributeList.forEach(c -> {
            ThreadContributeResDto contributeResDto = new ThreadContributeResDto();
            contributeResDto.setContributorUserId(userId);
            contributeResDto.setYear(String.valueOf(c.getCreatedDate().getYear()));
            contributeResDto.setMonth(String.valueOf(c.getCreatedDate().getMonth()));
            contributeResDto.setCreatedDate(c.getCreatedDate());
            contributeResDto.setThreadId(c.getThreadId());
            contributeResDto.setType(c.getThreadType());
            contributeResDto.setThreadTitle(c.getThreadTitle());

            contributeResDtoList.add(contributeResDto);
        });
        resDto.setContributeHistoryList(contributeResDtoList);

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Profile Info Found", resDto);
    }

    @Transactional
    public CommonResponse<ProfileResDto> updateProfile(HttpServletRequest request, ProfileUpdateReqDto profileUpdateReqDto) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.validateAccessToken(accessToken);
        String email = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "User Not Found.");
        }

        Profile profile = profileRepository.findByUser(user);
        if (profile == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "Profile Not Found.");
        }

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

        if (user == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "User Not Found.");
        }

        List<Thread> threadList = threadRepository.findAllByUserAndStatusOrderByCreatedDateDesc(user, ThreadStatus.대기.name());
        List<ThreadResDto> resDtoList = new ArrayList<>();
        threadList.forEach(t -> {
            ThreadResDto res = new ThreadResDto();
            res.setId(t.getId());
            res.setTitle(t.getThreadTitle());
            res.setSubTitle(t.getThreadSubTitle());
            res.setThumbnailUrl(t.getThumbnailUrl() == null ? "https://knit-document.s3.ap-northeast-2.amazonaws.com/thread/cover/cover1.png" : t.getThumbnailUrl());
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
            List<ThreadContributor> contributors = threadContributorRepository.findAllByThreadId(t.getId());
            List<String> contributorList = new ArrayList<>();
            contributors.forEach(c -> {
                Profile profile = profileRepository.findByUserId(c.getContributorUserId());
                if (profile != null) {
                    contributorList.add(profile.getNickname());
                }
            });
            res.setContributorList(contributorList);
            res.setLikeCount(threadLikeRepository.countAllByThreadId(t.getId()));

            resDtoList.add(res);
        });

        ThreadListResDto resDto = new ThreadListResDto();
        resDto.setCount(resDtoList.size());
        resDto.setThreads(resDtoList);

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "User 등재 전 대기문서 조회공 성공", resDto);
    }

    public CommonResponse<List<ThreadContributeResDto>> getUserContributeHistoryList(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.validateAccessToken(accessToken);
        String email = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "User Not Found.");
        }

        Long userId = user.getId();

        List<ThreadContributor> contributeList = threadContributorRepository.findAllByContributorUserId(user.getId());
        List<ThreadContributeResDto> resDtoList = new ArrayList<>();
        contributeList.forEach(c -> {
            ThreadContributeResDto resDto = new ThreadContributeResDto();
            resDto.setContributorUserId(userId);
            resDto.setYear(String.valueOf(c.getCreatedDate().getYear()));
            resDto.setMonth(String.valueOf(c.getCreatedDate().getMonth()));
            resDto.setCreatedDate(c.getCreatedDate());
            resDto.setThreadId(c.getThreadId());
            resDto.setType(c.getThreadType());
            resDto.setThreadTitle(c.getThreadTitle());

            resDtoList.add(resDto);
        });

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "User 기여 히스토리 조회 성공", resDtoList);
    }
}
