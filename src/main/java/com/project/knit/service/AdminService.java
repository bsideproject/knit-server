package com.project.knit.service;

import com.project.knit.domain.entity.Thread;
import com.project.knit.domain.repository.ThreadRepository;
import com.project.knit.dto.res.CategoryResDto;
import com.project.knit.dto.res.CommonResponse;
import com.project.knit.dto.res.ContentResDto;
import com.project.knit.dto.res.ReferenceResDto;
import com.project.knit.dto.res.TagResDto;
import com.project.knit.dto.res.ThreadAdminResDto;
import com.project.knit.utils.enums.StatusCodeEnum;
import com.project.knit.utils.enums.ThreadStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class AdminService {

    private final ThreadRepository threadRepository;

    public <T> CommonResponse<T> acceptThread(Long threadId) {
        Thread thread = threadRepository.findById(threadId).orElseThrow(() -> new NullPointerException("Thread Info Not Found."));
        thread.changeStatus(ThreadStatus.승인.name());

        threadRepository.save(thread);
        // todo
        // ThreadCategory save
        // ThreadTag save
        // ThreadReference save

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "[ADMIN] Thread Successfully Created.");
    }

    public <T> CommonResponse<T> declineThread(Long threadId) {
        Thread thread = threadRepository.findById(threadId).orElseThrow(() -> new NullPointerException("Thread Info Not Found."));
        thread.changeStatus(ThreadStatus.반려.name());

        threadRepository.save(thread);

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "[ADMIN] Thread Declined.");
    }

    public CommonResponse<List<ThreadAdminResDto>> getAllThreadList() {
        List<Thread> threadList = threadRepository.findAll();
        List<ThreadAdminResDto> resDtoList = new ArrayList<>();

        for (Thread t : threadList) {
            ThreadAdminResDto res = new ThreadAdminResDto();
            res.setId(t.getId());
            res.setTitle(t.getThreadTitle());
            res.setSubTitle(t.getThreadSubTitle());
            res.setThumbnailUrl(t.getThumbnailUrl());
            List<ContentResDto> contentList = new ArrayList<>();
            t.getContents().forEach(c -> {
                ContentResDto contentRes = new ContentResDto();
                contentRes.setContentId(c.getId());
                contentRes.setType(c.getContentType());
                contentRes.setValue(c.getValue());
                contentRes.setSummary(c.getSummary());

                contentList.add(contentRes);
            });
            res.setContents(contentList);
            List<CategoryResDto> categoryList = new ArrayList<>();
            t.getCategories().forEach(c -> {
                CategoryResDto categoryRes = new CategoryResDto();
                categoryRes.setCategoryId(c.getId());
                categoryRes.setValue(c.getCategory());

                categoryList.add(categoryRes);
            });
            res.setCategories(categoryList);
            List<TagResDto> tagList = new ArrayList<>();
            t.getTags().forEach(tag -> {
                TagResDto tagRes = new TagResDto();
                tagRes.setTagId(tag.getId());
                tagRes.setValue(tag.getTagName());

                tagList.add(tagRes);
            });
            res.setTags(tagList);
            List<ReferenceResDto> referenceList = new ArrayList<>();
            t.getReferences().forEach(r -> {
                ReferenceResDto referenceRes = new ReferenceResDto();
                referenceRes.setReferenceId(r.getId());
                referenceRes.setReferenceLink(r.getReferenceLink());
                referenceRes.setReferenceDescription(r.getReferenceDescription());

                referenceList.add(referenceRes);
            });
            res.setReferences(referenceList);
            res.setStatus(t.getStatus());
            res.setNickname("닉네임테스트");
            res.setCreatedDate(t.getCreatedDate());

            resDtoList.add(res);
        }

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "[ADMIN] All Thread List.", resDtoList);
    }

    public CommonResponse<List<ThreadAdminResDto>> getThreadListByStatus(String status) {
        List<Thread> threadList = threadRepository.findAllByStatusOrderByModifiedDateDesc(status);
        List<ThreadAdminResDto> resDtoList = new ArrayList<>();

        for (Thread t : threadList) {
            ThreadAdminResDto res = new ThreadAdminResDto();
            res.setId(t.getId());
            res.setTitle(t.getThreadTitle());
            res.setSubTitle(t.getThreadSubTitle());
            res.setThumbnailUrl(t.getThumbnailUrl());
            List<ContentResDto> contentList = new ArrayList<>();
            t.getContents().forEach(c -> {
                ContentResDto contentRes = new ContentResDto();
                contentRes.setContentId(c.getId());
                contentRes.setType(c.getContentType());
                contentRes.setValue(c.getValue());
                contentRes.setSummary(c.getSummary());

                contentList.add(contentRes);
            });
            res.setContents(contentList);
            List<CategoryResDto> categoryList = new ArrayList<>();
            t.getCategories().forEach(c -> {
                CategoryResDto categoryRes = new CategoryResDto();
                categoryRes.setCategoryId(c.getId());
                categoryRes.setValue(c.getCategory());

                categoryList.add(categoryRes);
            });
            res.setCategories(categoryList);
            List<TagResDto> tagList = new ArrayList<>();
            t.getTags().forEach(tag -> {
                TagResDto tagRes = new TagResDto();
                tagRes.setTagId(tag.getId());
                tagRes.setValue(tag.getTagName());

                tagList.add(tagRes);
            });
            res.setTags(tagList);
            List<ReferenceResDto> referenceList = new ArrayList<>();
            t.getReferences().forEach(r -> {
                ReferenceResDto referenceRes = new ReferenceResDto();
                referenceRes.setReferenceId(r.getId());
                referenceRes.setReferenceLink(r.getReferenceLink());
                referenceRes.setReferenceDescription(r.getReferenceDescription());

                referenceList.add(referenceRes);
            });
            res.setReferences(referenceList);
            res.setStatus(t.getStatus());
            res.setNickname("닉네임테스트");
            res.setCreatedDate(t.getCreatedDate());

            resDtoList.add(res);
        }

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "[ADMIN] All Thread List By Status.", resDtoList);
    }
}
