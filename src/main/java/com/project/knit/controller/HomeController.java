package com.project.knit.controller;

import com.project.knit.dto.res.*;
import com.project.knit.service.S3Service;
import com.project.knit.service.ThreadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
    @GetMapping("/search")
    public ResponseEntity<CommonResponse<ThreadPagingResDto>> getKeywordSearchList(@RequestParam String search, @RequestParam(defaultValue = "0") Integer page) {
        return new ResponseEntity<>(threadService.getSearchList(search, page), HttpStatus.OK);
    }

    // 모아보기
    @GetMapping("/collection/{type}")
    public ResponseEntity<CommonResponse<ThreadPagingResDto>> getCollectionList(@PathVariable @Valid @NotBlank(message = "Type should not be null or empty.") String type, @RequestParam(defaultValue = "0") Integer page) {
        return new ResponseEntity<>(threadService.getCollectionList(type, page), HttpStatus.OK);
    }

    // 직군-문서
    @GetMapping("/{category}/{type}")
    public ResponseEntity<CommonResponse<ThreadPagingResDto>> getGroupList(@PathVariable @Valid @NotBlank(message = "Category should not be null or empty.") String category, @PathVariable @Valid @NotBlank(message = "Type should not be null or empty.") String type, @RequestParam(defaultValue = "0") Integer page) {
        return new ResponseEntity<>(threadService.getGroupList(category, type, page), HttpStatus.OK);
    }
}
