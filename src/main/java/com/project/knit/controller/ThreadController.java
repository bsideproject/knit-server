package com.project.knit.controller;

import com.project.knit.dto.req.ThreadCreateReqDto;
import com.project.knit.dto.res.CategoryResDto;
import com.project.knit.dto.res.CommonResponse;
import com.project.knit.dto.res.S3ImageResDto;
import com.project.knit.dto.res.TagResDto;
import com.project.knit.dto.res.ThreadListResDto;
import com.project.knit.dto.res.ThreadResDto;
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

import java.io.IOException;
import java.util.List;


@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
public class ThreadController {

    private final ThreadService threadService;
    private final S3Service s3Service;

    @GetMapping("/thread/{threadId}")
    public ResponseEntity<ThreadResDto> getThreadInfoById(@PathVariable Long threadId) {
        return new ResponseEntity<>(threadService.getThreadInfoById(threadId), HttpStatus.OK);
    }

    @PostMapping("/v1/threads/register")
    public ResponseEntity<CommonResponse> registerThread(@RequestBody ThreadCreateReqDto threadCreateReqDto) {
        return new ResponseEntity<>(threadService.registerThread(threadCreateReqDto), HttpStatus.OK);
    }

//    @PostMapping("/v1/threads/update/{threadId}")
//    public ResponseEntity<CommonResponse> updateRegisterThread(@PathVariable Long threadId, @RequestBody ThreadUpdateReqDto threadUpdateReqDto) {
//        return new ResponseEntity<>(threadService.updateRegisterThread(threadId, threadUpdateReqDto), HttpStatus.OK);
//    }

    @GetMapping("/v1/threads/tag/{tagId}")
    public ResponseEntity<ThreadListResDto> getThreadListByTagId(@PathVariable Long tagId) {
        return new ResponseEntity<>(threadService.getThreadListByTagId(tagId), HttpStatus.OK);
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
    public ResponseEntity<List<TagResDto>> getAllTags() {
        return new ResponseEntity<>(threadService.getAllTags(), HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResDto>> getAllCategories() {
        return new ResponseEntity<>(threadService.getAllCategories(), HttpStatus.OK);
    }
}
