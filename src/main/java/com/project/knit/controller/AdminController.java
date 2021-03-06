package com.project.knit.controller;

import com.project.knit.dto.req.ThreadDeclineReqDto;
import com.project.knit.dto.res.CommonResponse;
import com.project.knit.dto.res.ThreadAdminResDto;
import com.project.knit.dto.res.ThreadResDto;
import com.project.knit.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@RestController
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/accept/{threadId}")
    public ResponseEntity<CommonResponse> acceptThread(@PathVariable Long threadId) {
        return new ResponseEntity<>(adminService.acceptThread(threadId), HttpStatus.OK);
    }

    @PostMapping("/reject/{threadId}")
    public ResponseEntity<CommonResponse> declineThread(@PathVariable Long threadId, @Valid @RequestBody ThreadDeclineReqDto threadDeclineReqDto) {
        return new ResponseEntity<>(adminService.declineThread(threadId, threadDeclineReqDto), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<CommonResponse<List<ThreadAdminResDto>>> getAllThreadList() {
        return new ResponseEntity<>(adminService.getAllThreadList(), HttpStatus.OK);
    }

    @GetMapping("/waiting")
    public ResponseEntity<CommonResponse<List<ThreadAdminResDto>>> getWaitingThreadList() {
        return new ResponseEntity<>(adminService.getThreadListByStatus("대기"), HttpStatus.OK);
    }

    @GetMapping("/accepted")
    public ResponseEntity<CommonResponse<List<ThreadAdminResDto>>> getAcceptedThreadList() {
        return new ResponseEntity<>(adminService.getThreadListByStatus("승인"), HttpStatus.OK);
    }

    @GetMapping("/declined")
    public ResponseEntity<CommonResponse<List<ThreadAdminResDto>>> getDeclinedThreadList() {
        return new ResponseEntity<>(adminService.getThreadListByStatus("반려"), HttpStatus.OK);
    }

    @GetMapping("/thread/{threadId}")
    public ResponseEntity<CommonResponse<ThreadResDto>> getThreadInfoById(@PathVariable Long threadId) {
        return new ResponseEntity<>(adminService.getThreadInfoById(threadId), HttpStatus.OK);
    }

    @PostMapping("/feature/{threadId}")
    public ResponseEntity<CommonResponse> registerToFeature(@PathVariable Long threadId) {
        return new ResponseEntity<>(adminService.registerToFeature(threadId), HttpStatus.OK);
    }
}
