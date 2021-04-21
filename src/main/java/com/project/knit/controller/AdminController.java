package com.project.knit.controller;

import com.project.knit.dto.res.CommonResponse;
import com.project.knit.dto.res.ThreadAdminResDto;
import com.project.knit.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<CommonResponse> declineThread(@PathVariable Long threadId) {
        return new ResponseEntity<>(adminService.declineThread(threadId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ThreadAdminResDto>> getAllThreadList() {
        return new ResponseEntity<>(adminService.getAllThreadList(), HttpStatus.OK);
    }

    @GetMapping("/waiting")
    public ResponseEntity<List<ThreadAdminResDto>> getWaitingThreadList() {
        return new ResponseEntity<>(adminService.getThreadListByStatus("대기"), HttpStatus.OK);
    }

    @GetMapping("/accepted")
    public ResponseEntity<List<ThreadAdminResDto>> getAcceptedThreadList() {
        return new ResponseEntity<>(adminService.getThreadListByStatus("승인"), HttpStatus.OK);
    }

    @GetMapping("/declined")
    public ResponseEntity<List<ThreadAdminResDto>> getDeclinedThreadList() {
        return new ResponseEntity<>(adminService.getThreadListByStatus("반려"), HttpStatus.OK);
    }
}
