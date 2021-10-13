package com.project.knit.controller;

import com.project.knit.dto.req.ProfileUpdateReqDto;
import com.project.knit.dto.res.*;
import com.project.knit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@Validated
@RequestMapping("/v1/user")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    // login + create profile
    @PostMapping("/login/{type}")
    public ResponseEntity<CommonResponse<LoginResDto>> login(HttpServletRequest request, @PathVariable @Valid @NotBlank(message = "SNS type should not be null or empty.") String type) {
        return new ResponseEntity<>(userService.login(request, type), HttpStatus.OK);
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<CommonResponse<LoginResDto>> refreshToken(HttpServletRequest request) {
        CommonResponse<LoginResDto> response = userService.refreshToken(request);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    // logout
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(HttpServletRequest request) {
        return new ResponseEntity<>(userService.logout(request), HttpStatus.OK);
    }

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

    // 기여 history
    @GetMapping("/contribute/history")
    public ResponseEntity<CommonResponse<List<ThreadContributeResDto>>> getUserContributeHistoryList(HttpServletRequest request) {
        return new ResponseEntity<>(userService.getUserContributeHistoryList(request), HttpStatus.OK);
    }
}
