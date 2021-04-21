package com.project.knit.service;

import com.project.knit.domain.entity.Thread;
import com.project.knit.domain.entity.ThreadCategory;
import com.project.knit.domain.entity.ThreadReference;
import com.project.knit.domain.entity.ThreadTag;
import com.project.knit.domain.repository.ThreadCategoryRepository;
import com.project.knit.domain.repository.ThreadReferenceRepository;
import com.project.knit.domain.repository.ThreadRepository;
import com.project.knit.domain.repository.ThreadTagRepository;
import com.project.knit.dto.res.CommonResponse;
import com.project.knit.dto.res.ThreadAdminResDto;
import com.project.knit.utils.enums.ThreadStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class AdminService {

    private final ThreadRepository threadRepository;
    private final ThreadCategoryRepository threadCategoryRepository;
    private final ThreadTagRepository threadTagRepository;

    @Transactional
    public CommonResponse acceptThread(Long threadId) {
        Thread thread = threadRepository.getOne(threadId);
        thread.changeStatus(ThreadStatus.생성승인.name());

        threadRepository.save(thread);
        // ThreadCategory save
        thread.getCategoryList().forEach(c -> {
            ThreadCategory threadCategory = ThreadCategory.builder()
                    .threadId(c.getThread().getId())
                    .categoryId(c.getId())
                    .build();
            threadCategoryRepository.save(threadCategory);
        });

        // ThreadTag save
        thread.getTagList().forEach(t -> {
            ThreadTag threadTag = ThreadTag.builder()
                    .threadId(t.getThread().getId())
                    .tagId(t.getId())
                    .build();

            threadTagRepository.save(threadTag);
        });

        CommonResponse response = new CommonResponse();
        response.setMessage("Thread Successfully Created.");

        return response;
    }

    public CommonResponse declineThread(Long threadId) {
        Thread thread = threadRepository.getOne(threadId);
        thread.changeStatus(ThreadStatus.생성반려.name());

        threadRepository.save(thread);

        CommonResponse response = new CommonResponse();
        response.setMessage("Thread Declined.");

        return response;
    }

    public List<ThreadAdminResDto> getAllThreadList() {
        List<Thread> threadList = threadRepository.findAll();
        List<ThreadAdminResDto> resDtoList = new ArrayList<>();

        for(Thread t : threadList) {
            ThreadAdminResDto res = new ThreadAdminResDto();
            res.setThreadId(t.getId());
            res.setThreadTitle(t.getThreadTitle());
            res.setThreadSubTitle(t.getThreadSubTitle());
            res.setThreadThumbnail(t.getThreadThumbnail());
            res.setContentList(t.getContentList());
            res.setCategoryList(t.getCategoryList());
            res.setTagList(t.getTagList());
            res.setThreadReferenceList(t.getThreadReferenceList());
            res.setStatus(t.getStatus());
            res.setNickname("닉네임테스트");

            resDtoList.add(res);
        }

        return resDtoList;
    }

    public List<ThreadAdminResDto> getThreadListByStatus(String status) {
        List<Thread> threadList = threadRepository.findAllByStatus(status);
        List<ThreadAdminResDto> resDtoList = new ArrayList<>();

        for(Thread t : threadList) {
            ThreadAdminResDto res = new ThreadAdminResDto();
            res.setThreadId(t.getId());
            res.setThreadTitle(t.getThreadTitle());
            res.setThreadSubTitle(t.getThreadSubTitle());
            res.setThreadThumbnail(t.getThreadThumbnail());
            res.setContentList(t.getContentList());
            res.setCategoryList(t.getCategoryList());
            res.setTagList(t.getTagList());
            res.setThreadReferenceList(t.getThreadReferenceList());
            res.setStatus(t.getStatus());
            res.setNickname("닉네임테스트");

            resDtoList.add(res);
        }

        return resDtoList;
    }
}
