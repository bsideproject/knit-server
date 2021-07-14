package com.project.knit.controller;

import com.project.knit.dto.req.LoginReqDto;
import com.project.knit.dto.req.ProfileUpdateReqDto;
import com.project.knit.dto.res.CommonResponse;
import com.project.knit.dto.res.LoginResDto;
import com.project.knit.dto.res.ProfileResDto;
import com.project.knit.dto.res.ThreadListResDto;
import com.project.knit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@Validated
@RequestMapping("/v1/user")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    // login + create profile
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResDto>> login(@Valid @RequestBody LoginReqDto loginReqDto) {
        return new ResponseEntity<>(userService.login(loginReqDto), HttpStatus.OK);
    }

    // logout

    // getProfileInfo
    @GetMapping("/profile")
    public ResponseEntity<CommonResponse<ProfileResDto>> getProfileInfo(HttpServletRequest request) {
        return new ResponseEntity<>(userService.getProfileInfo(request), HttpStatus.OK);
    }

    // 등재전대기문서
    @GetMapping("/waiting")
    public ResponseEntity<CommonResponse<ThreadListResDto>> getUserWaitingThreadList(HttpServletRequest request) {
        return new ResponseEntity<>(userService.getUserWaitingThreadList(request), HttpStatus.OK);
    }

    // profile update
    @PostMapping("/profile")
    public ResponseEntity<CommonResponse<ProfileResDto>> updateProfile(HttpServletRequest request, ProfileUpdateReqDto profileUpdateReqDto) {
        return new ResponseEntity<>(userService.updateProfile(request, profileUpdateReqDto), HttpStatus.OK);
    }
}
