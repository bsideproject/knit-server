package com.project.knit.service;

import com.project.knit.domain.entity.Category;
import com.project.knit.domain.entity.Content;
import com.project.knit.domain.entity.ThreadReference;
import com.project.knit.domain.entity.Tag;
import com.project.knit.domain.entity.Thread;
import com.project.knit.domain.repository.CategoryRepository;
import com.project.knit.domain.repository.ContentRepository;
import com.project.knit.domain.repository.TagRepository;
import com.project.knit.domain.repository.ThreadReferenceRepository;
import com.project.knit.domain.repository.ThreadRepository;
import com.project.knit.dto.req.ThreadCreateReqDto;
import com.project.knit.dto.req.ThreadUpdateReqDto;
import com.project.knit.dto.res.CategoryResDto;
import com.project.knit.dto.res.CommonResponse;
import com.project.knit.dto.res.ContentResDto;
import com.project.knit.dto.res.ThreadReferenceResDto;
import com.project.knit.dto.res.TagResDto;
import com.project.knit.dto.res.ThreadAdminResDto;
import com.project.knit.dto.res.ThreadCreateResDto;
import com.project.knit.dto.res.ThreadListResDto;
import com.project.knit.dto.res.ThreadResDto;
import com.project.knit.utils.enums.ThreadStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ThreadService {

    private final ThreadRepository threadRepository;
    private final ContentRepository contentRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final ThreadReferenceRepository threadReferenceRepository;

    private final S3Service s3Service;
    private final AdminService adminService;

    public CommonResponse checkTagName(String tagName) {
        CommonResponse response = new CommonResponse();
        Tag tag = tagRepository.findByTagName(tagName);
        if (tag != null) {
            response.setMessage("Already Exists.");
        } else {
            response.setMessage("Available Tag Name.");
        }

        return response;
    }

    public ThreadResDto getThreadInfoById(Long id) {
        Thread thread = threadRepository.findById(id)
                .orElseThrow(NullPointerException::new);
        Long threadId = thread.getId();

        List<Content> contentList = contentRepository.findAllByThreadId(threadId);
        List<Category> categoryList = categoryRepository.findAllByThreadId(threadId);
        List<Tag> tagList = tagRepository.findAllByThreadId(threadId);
        List<ThreadReference> threadReferenceList = threadReferenceRepository.findAllByThreadId(threadId);

        List<ContentResDto> contentResList = new ArrayList<>();
        List<CategoryResDto> categoryResList = new ArrayList<>();
        List<TagResDto> tagResList = new ArrayList<>();
        List<ThreadReferenceResDto> threadReferenceResList = new ArrayList<>();

        for (Content c : contentList) {
            ContentResDto res = new ContentResDto();
            res.setContentId(c.getId());
            res.setType(c.getThreadType().name());
            res.setValue(c.getValue());
            res.setSummary(c.getSummary() == null ? null : c.getSummary());

            contentResList.add(res);
        }
        for (Category c : categoryList) {
            CategoryResDto res = new CategoryResDto();
            res.setCategoryId(c.getId());
            res.setCategory(c.getCategory());

            categoryResList.add(res);
        }
        for (Tag t : tagList) {
            TagResDto res = new TagResDto();
            res.setTagId(t.getId());
            res.setTag(t.getTagName());

            tagResList.add(res);
        }
        for (ThreadReference r : threadReferenceList) {
            ThreadReferenceResDto res = new ThreadReferenceResDto();
            res.setReferenceId(r.getId());
            res.setReferenceLink(r.getReferenceLink());
            res.setReferenceDescription(r.getReferenceDescription());

            threadReferenceResList.add(res);
        }

        ThreadResDto resDto = new ThreadResDto();
        resDto.setCategoryList(categoryResList);
        resDto.setContentList(contentResList);
        resDto.setReferenceList(threadReferenceResList);
        resDto.setTagList(tagResList);
        resDto.setThreadId(thread.getId());
        resDto.setThreadTitle(thread.getThreadTitle());
        resDto.setThreadSubTitle(thread.getThreadSubTitle());
        resDto.setThreadThumbnail(thread.getThreadThumbnail());

        return resDto;
    }

    @Transactional
    public CommonResponse registerThread(ThreadCreateReqDto threadCreateReqDto) {

        threadCreateReqDto.getTagList().forEach(t -> {
            checkTagName(t.getTagName());
        });

        Thread thread = Thread.builder()
                .threadTitle(threadCreateReqDto.getTitle())
                .threadSubTitle(threadCreateReqDto.getSubTitle())
                .threadThumbnail(threadCreateReqDto.getThumbnail())
                .threadSummary(threadCreateReqDto.getSummary())
                .contentList(threadCreateReqDto.getContentList())
                .threadReferenceList(threadCreateReqDto.getThreadReferenceList())
                .tagList(threadCreateReqDto.getTagList())
                .categoryList(threadCreateReqDto.getCategoryList())
                .status(ThreadStatus.생성대기.name())
                .build();

        Thread createdThread = threadRepository.save(thread);
        // todo content object 하나만 가지도록 (리스트 X)
        for (Content c : threadCreateReqDto.getContentList()) {
            contentRepository.save(c);
            c.addThread(createdThread);
        }
        for (Tag t : threadCreateReqDto.getTagList()) {
            tagRepository.save(t);
            t.addThread(createdThread);
        }
        for (Category c : threadCreateReqDto.getCategoryList()) {
            categoryRepository.save(c);
            c.addThread(createdThread);
        }
        for (ThreadReference r : threadCreateReqDto.getThreadReferenceList()) {
            threadReferenceRepository.save(r);
            r.addThread(createdThread);
        }

        CommonResponse resDto = new CommonResponse();
        resDto.setMessage("Thread on the waiting list.");
        return resDto;
    }

    // 조회수 (최근 조회된 문서 리스트)
    // 마이 페이지
    // temp register 임시저장 (token) user 하나당
    // todo temp 테이블 5개 CRUD + 조회
    // 캐쉬 서버

    @Transactional
    public CommonResponse registerUpdateThread(Long threadId, ThreadUpdateReqDto threadUpdateReqDto) {
        Thread foundThread = threadRepository.findById(threadId)
                .orElseThrow(() -> new NullPointerException("Thread Not Found."));

        foundThread.update(threadUpdateReqDto, foundThread);
        foundThread.changeStatus(ThreadStatus.수정대기.name());

        foundThread.getContentList().forEach(c -> {
            Content content = contentRepository.findById(c.getId()).orElse(null);
            if(content != null) {
                content.update(content, c);
            } else {
                contentRepository.save(c);
                c.addThread(foundThread);
            }
        });

        CommonResponse resDto = new CommonResponse();
        resDto.setMessage("Thread on the waiting list.");
        return resDto;
    }

    public ThreadListResDto getThreadListByTagId(Long tagId) {
        Tag tag = tagRepository.getOne(tagId);
        List<Tag> tagList = new ArrayList<>();
        tagList.add(tag);
        List<ThreadAdminResDto> resDtoList = new ArrayList<>();

        List<Thread> threadList = threadRepository.findAllByTagListIn(tagList);
        for (Thread d : threadList) {
            ThreadAdminResDto res = new ThreadAdminResDto();

            res.setNickname("테스트닉네임");
            res.setThreadId(d.getId());
            res.setThreadTitle(d.getThreadTitle());
            res.setThreadSubTitle(d.getThreadSubTitle());
            res.setThreadThumbnail(d.getThreadThumbnail());
            res.setTagList(d.getTagList());
            res.setThreadReferenceList(d.getThreadReferenceList());
//            res.setContentList(d.getContentList());
//            res.setCategoryList(d.getCategoryList());

            resDtoList.add(res);
        }
        ThreadListResDto res = new ThreadListResDto();
        res.setCount(resDtoList.size());
        res.setThreadList(resDtoList);

        return res;
    }

    public List<TagResDto> getAllTags() {
        List<Tag> tagList = tagRepository.findAll();
        List<TagResDto> resDtoList = new ArrayList<>();
        tagList.forEach(tag -> {
            TagResDto res = new TagResDto();
            res.setTagId(tag.getId());
            res.setTag(tag.getTagName());

            resDtoList.add(res);
        });
        return resDtoList;
    }

    public List<CategoryResDto> getAllCategories() {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryResDto> resDtoList = new ArrayList<>();
        categoryList.forEach(c -> {
            CategoryResDto res = new CategoryResDto();
            res.setCategoryId(c.getId());
            res.setCategory(c.getCategory());

            resDtoList.add(res);
        });

        return resDtoList;
    }
}
