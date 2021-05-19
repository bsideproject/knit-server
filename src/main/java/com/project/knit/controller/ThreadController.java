package com.project.knit.controller;

import com.project.knit.dto.req.ThreadCreateReqDto;
import com.project.knit.dto.res.*;
import com.project.knit.service.S3Service;
import com.project.knit.service.ThreadService;
import lombok.Getter;
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
    public ResponseEntity<CommonResponse> registerThread(@Valid @RequestBody ThreadCreateReqDto threadCreateReqDto) {
        return new ResponseEntity<>(threadService.registerThread(threadCreateReqDto), HttpStatus.OK);
    }

//    @PostMapping("/v1/threads/update/{threadId}")
//    public ResponseEntity<CommonResponse> updateRegisterThread(@PathVariable Long threadId, @Valid @RequestBody ThreadUpdateReqDto threadUpdateReqDto) {
//        return new ResponseEntity<>(threadService.updateRegisterThread(threadId, threadUpdateReqDto), HttpStatus.OK);
//    }

    @GetMapping("/v1/threads/tag/{tagId}")
    public ResponseEntity<CommonResponse<ThreadListResDto>> getThreadListByTagId(@PathVariable Long tagId) {
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
    public ResponseEntity<CommonResponse<List<TagResDto>>> getAllTags() {
        return new ResponseEntity<>(threadService.getAllTags(), HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<CommonResponse<List<CategoryResDto>>> getAllCategories() {
        return new ResponseEntity<>(threadService.getAllCategories(), HttpStatus.OK);
    }
}
