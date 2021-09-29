package com.project.knit.controller;

import com.project.knit.dto.req.ThreadCreateReqDto;
import com.project.knit.dto.req.ThreadLikeReqDto;
import com.project.knit.dto.req.ThreadUpdateReqDto;
import com.project.knit.dto.res.CategoryResDto;
import com.project.knit.dto.res.CommonResponse;
import com.project.knit.dto.res.S3ImageResDto;
import com.project.knit.dto.res.TagResDto;
import com.project.knit.dto.res.ThreadListResDto;
import com.project.knit.dto.res.ThreadResDto;
import com.project.knit.dto.res.ThreadShortListResDto;
import com.project.knit.service.S3Service;
import com.project.knit.service.ThreadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
public class ThreadController {

    private final ThreadService threadService;
    private final S3Service s3Service;

    @GetMapping("/v1/threads/list")
    public ResponseEntity<CommonResponse<ThreadShortListResDto>> getThreadInfoList() {
        return new ResponseEntity<>(threadService.getThreadInfoList(), HttpStatus.OK);
    }

    @GetMapping("/thread/{threadId}")
    public ResponseEntity<CommonResponse<ThreadResDto>> getThreadInfoById(@PathVariable Long threadId) {
        return new ResponseEntity<>(threadService.getThreadInfoById(threadId), HttpStatus.OK);
    }

    @PostMapping("/v1/threads/register")
    public ResponseEntity<CommonResponse> registerThread(@Valid @RequestBody ThreadCreateReqDto threadCreateReqDto, HttpServletRequest request) {
        return new ResponseEntity<>(threadService.registerThread(threadCreateReqDto, request), HttpStatus.OK);
    }

    @PostMapping("/v1/threads/update/{threadId}")
    public ResponseEntity<CommonResponse> updateRegisterThread(@PathVariable Long threadId, @Valid @RequestBody ThreadUpdateReqDto threadUpdateReqDto, HttpServletRequest request) {
        return new ResponseEntity<>(threadService.updateThread(threadId, threadUpdateReqDto, request), HttpStatus.OK);
    }

    @GetMapping("/v1/threads/tag/{tagId}")
    public ResponseEntity<CommonResponse<ThreadListResDto>> getThreadListByTagId(@PathVariable Long tagId, HttpServletRequest request) {
        return new ResponseEntity<>(threadService.getThreadListByTagId(tagId, request), HttpStatus.OK);
    }

    @GetMapping("/v1/threads/tag/validation")
    public ResponseEntity<CommonResponse> checkTagName(@RequestParam(value = "tag") String tagName) {
        return new ResponseEntity<>(threadService.checkTagName(tagName), HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<S3ImageResDto> upload(@RequestPart(value = "file") MultipartFile multipartFile, @RequestPart(value = "type") String type) throws IOException {
        return new ResponseEntity<>(s3Service.upload(multipartFile, type), HttpStatus.OK);
    }

    @GetMapping("/tags")
    public ResponseEntity<CommonResponse<List<TagResDto>>> getAllTags() {
        return new ResponseEntity<>(threadService.getAllTags(), HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<CommonResponse<List<CategoryResDto>>> getAllCategories() {
        return new ResponseEntity<>(threadService.getAllCategories(), HttpStatus.OK);
    }

    @PostMapping("/v1/threads/like")
    public ResponseEntity<CommonResponse> likeThread(@Valid @RequestBody ThreadLikeReqDto threadLikeReqDto, HttpServletRequest request) {
        return new ResponseEntity<>(threadService.likeThread(threadLikeReqDto, request), HttpStatus.OK);
    }

    @PostMapping("/v1/threads/like/cancel")
    public ResponseEntity<CommonResponse> cancelLikeThread(@Valid @RequestBody ThreadLikeReqDto threadLikeReqDto, HttpServletRequest request) {
        return new ResponseEntity<>(threadService.cancelLikeThread(threadLikeReqDto, request), HttpStatus.OK);
    }
}
