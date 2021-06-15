package com.project.knit.controller;

import com.project.knit.dto.res.*;
import com.project.knit.service.S3Service;
import com.project.knit.service.ThreadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RequestMapping("/v1/home")
@RequiredArgsConstructor
@RestController
public class HomeController {

    private final ThreadService threadService;
    private final S3Service s3Service;

    // 가장 많이 조회된 문서 조회 (6)
    @GetMapping("/most/viewed")
    public ResponseEntity<CommonResponse<ThreadMostViewedListResDto>> getMostViewedList() {
        return new ResponseEntity<>(threadService.getMostViewedList(), HttpStatus.OK);
    }

    // 최근 변경된 문서 (10)
    @GetMapping("/recent/changed")
    public ResponseEntity<CommonResponse<List<ThreadRecentChangedResDto>>> getRecentChangedList() {
        return new ResponseEntity<>(threadService.getRecentChangedList(), HttpStatus.OK);
    }

    // featured
    @GetMapping("/featured")
    public ResponseEntity<CommonResponse<ThreadFeaturedResDto>> getFeaturedThread() {
        return new ResponseEntity<>(threadService.getFeaturedThread(), HttpStatus.OK);
    }

    // search
//    @GetMapping("/search/{keyword}")
//    public ResponseEntity<List<>>

    // search by tag

}
