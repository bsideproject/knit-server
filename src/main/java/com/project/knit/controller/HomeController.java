package com.project.knit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RequestMapping("/v1/home")
@RequiredArgsConstructor
@RestController
public class HomeController {

    // 가장 많이 조회된 문서 조회
    // 최근 변경된 문서


}
