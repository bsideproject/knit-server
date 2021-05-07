package com.project.knit.service;

import com.project.knit.domain.entity.Thread;
import com.project.knit.domain.repository.ThreadRepository;
import com.project.knit.dto.res.CategoryResDto;
import com.project.knit.dto.res.CommonResponse;
import com.project.knit.dto.res.ContentResDto;
import com.project.knit.dto.res.ReferenceResDto;
import com.project.knit.dto.res.TagResDto;
import com.project.knit.dto.res.ThreadAdminResDto;
import com.project.knit.utils.enums.ThreadStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class AdminService {

    private final ThreadRepository threadRepository;

    public CommonResponse acceptThread(Long threadId) {
        Thread thread = threadRepository.findById(threadId).orElseThrow(() -> new NullPointerException("Thread Info Not Found."));
        thread.changeStatus(ThreadStatus.승인.name());

        threadRepository.save(thread);
        // ThreadCategory save
        // ThreadTag save
        // ThreadReference save

        CommonResponse response = new CommonResponse();
        response.setMessage("Thread Successfully Created.");

        return response;
    }

    public CommonResponse declineThread(Long threadId) {
        Thread thread = threadRepository.findById(threadId).orElseThrow(() -> new NullPointerException("Thread Info Not Found."));
        thread.changeStatus(ThreadStatus.반려.name());

        threadRepository.save(thread);

        CommonResponse response = new CommonResponse();
        response.setMessage("Thread Declined.");

        return response;
    }

    public List<ThreadAdminResDto> getAllThreadList() {
        List<Thread> threadList = threadRepository.findAll();
        List<ThreadAdminResDto> resDtoList = new ArrayList<>();

        for (Thread t : threadList) {
            ThreadAdminResDto res = new ThreadAdminResDto();
            res.setThreadId(t.getId());
            res.setThreadTitle(t.getThreadTitle());
            res.setThreadSubTitle(t.getThreadSubTitle());
            res.setThreadThumbnail(t.getThreadThumbnail());
            List<ContentResDto> contentList = new ArrayList<>();
            t.getContentList().forEach(c -> {
                ContentResDto contentRes = new ContentResDto();
                contentRes.setContentId(c.getId());
                contentRes.setType(c.getThreadType());
                contentRes.setValue(c.getValue());
                contentRes.setSummary(c.getSummary());

                contentList.add(contentRes);
            });
            res.setContentList(contentList);
            List<CategoryResDto> categoryList = new ArrayList<>();
            t.getCategoryList().forEach(c -> {
                CategoryResDto categoryRes = new CategoryResDto();
                categoryRes.setCategoryId(c.getId());
                categoryRes.setCategory(c.getCategory());

                categoryList.add(categoryRes);
            });
            res.setCategoryList(categoryList);
            List<TagResDto> tagList = new ArrayList<>();
            t.getTagList().forEach(tag -> {
                TagResDto tagRes = new TagResDto();
                tagRes.setTagId(tag.getId());
                tagRes.setTag(tag.getTagName());

                tagList.add(tagRes);
            });
            res.setTagList(tagList);
            List<ReferenceResDto> referenceList = new ArrayList<>();
            t.getReferenceList().forEach(r -> {
                ReferenceResDto referenceRes = new ReferenceResDto();
                referenceRes.setReferenceId(r.getId());
                referenceRes.setReferenceLink(r.getReferenceLink());
                referenceRes.setReferenceDescription(r.getReferenceDescription());

                referenceList.add(referenceRes);
            });
            res.setReferenceList(referenceList);
            res.setStatus(t.getStatus());
            res.setNickname("닉네임테스트");
            res.setCreatedDate(t.getCreatedDate());

            resDtoList.add(res);
        }

        return resDtoList;
    }

    public List<ThreadAdminResDto> getThreadListByStatus(String status) {
        List<Thread> threadList = threadRepository.findAllByStatusOrderByModifiedDateDesc(status);
        List<ThreadAdminResDto> resDtoList = new ArrayList<>();

        for (Thread t : threadList) {
            ThreadAdminResDto res = new ThreadAdminResDto();
            res.setThreadId(t.getId());
            res.setThreadTitle(t.getThreadTitle());
            res.setThreadSubTitle(t.getThreadSubTitle());
            res.setThreadThumbnail(t.getThreadThumbnail());
            List<ContentResDto> contentList = new ArrayList<>();
            t.getContentList().forEach(c -> {
                ContentResDto contentRes = new ContentResDto();
                contentRes.setContentId(c.getId());
                contentRes.setType(c.getThreadType());
                contentRes.setValue(c.getValue());
                contentRes.setSummary(c.getSummary());

                contentList.add(contentRes);
            });
            res.setContentList(contentList);
            List<CategoryResDto> categoryList = new ArrayList<>();
            t.getCategoryList().forEach(c -> {
                CategoryResDto categoryRes = new CategoryResDto();
                categoryRes.setCategoryId(c.getId());
                categoryRes.setCategory(c.getCategory());

                categoryList.add(categoryRes);
            });
            res.setCategoryList(categoryList);
            List<TagResDto> tagList = new ArrayList<>();
            t.getTagList().forEach(tag -> {
                TagResDto tagRes = new TagResDto();
                tagRes.setTagId(tag.getId());
                tagRes.setTag(tag.getTagName());

                tagList.add(tagRes);
            });
            res.setTagList(tagList);
            List<ReferenceResDto> referenceList = new ArrayList<>();
            t.getReferenceList().forEach(r -> {
                ReferenceResDto referenceRes = new ReferenceResDto();
                referenceRes.setReferenceId(r.getId());
                referenceRes.setReferenceLink(r.getReferenceLink());
                referenceRes.setReferenceDescription(r.getReferenceDescription());

                referenceList.add(referenceRes);
            });
            res.setReferenceList(referenceList);
            res.setStatus(t.getStatus());
            res.setNickname("닉네임테스트");
            res.setCreatedDate(t.getCreatedDate());

            resDtoList.add(res);
        }

        return resDtoList;
    }
}
