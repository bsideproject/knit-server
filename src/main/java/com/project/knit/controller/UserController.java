package com.project.knit.controller;

import com.project.knit.dto.req.LoginReqDto;
import com.project.knit.dto.res.CommonResponse;
import com.project.knit.service.UserService;
import com.project.knit.utils.enums.SocialLoginType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@Validated
@RequestMapping("/v1/user")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/auth/{socialLoginType}")
    public void socialLoginType(
            @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) {
        log.info(">> 사용자로부터 SNS 로그인 요청을 받음 :: {} Social Login", socialLoginType);
        userService.request(socialLoginType);
    }

    @GetMapping(value = "/auth/{socialLoginType}/callback")
    public String callback(
            @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
            @RequestParam(name = "code") String code) {
        log.info(">> 소셜 로그인 API 서버로부터 받은 code :: {}", code);
        // todo code를 user table에 저장
        return userService.requestAccessToken(socialLoginType, code);
    }

    // login + create profile
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<String>> login(@Valid @RequestBody LoginReqDto loginReqDto) {
        return new ResponseEntity<>(userService.login(loginReqDto), HttpStatus.OK);
    }


    // logout

    // getProfileInfo

    //
}
