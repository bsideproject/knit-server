package com.project.knit.service;

import com.project.knit.domain.entity.*;
import com.project.knit.domain.entity.Thread;
import com.project.knit.domain.repository.*;
import com.project.knit.dto.res.*;
import com.project.knit.utils.enums.StatusCodeEnum;
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
    private final ThreadReferenceRepository threadReferenceRepository;
    private final ContentRepository contentRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final ReferenceRepository referenceRepository;

    public <T> CommonResponse<T> acceptThread(Long threadId) {
        Thread thread = threadRepository.findById(threadId).orElseThrow(() -> new NullPointerException("Thread Info Not Found."));
        thread.changeStatus(ThreadStatus.승인.name());

        Thread acceptedThreaad = threadRepository.save(thread);
        // ThreadCategory save
        acceptedThreaad.getCategories().forEach(c -> {
            ThreadCategory threadCategory = ThreadCategory.builder()
                    .threadId(acceptedThreaad.getId())
                    .categoryId(c.getId())
                    .build();

            threadCategoryRepository.save(threadCategory);
        });

        acceptedThreaad.getTags().forEach(t -> {
            ThreadTag threadTag = ThreadTag.builder()
                    .threadId(acceptedThreaad.getId())
                    .tagId(t.getId())
                    .build();

            threadTagRepository.save(threadTag);
        });

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

    @Transactional
    public CommonResponse<ThreadResDto> getThreadInfoById(Long id) {
        Thread thread = threadRepository.findById(id).orElse(null);
        if (thread == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "Thread Not Found.");
        }
        Long threadId = thread.getId();

        List<Content> contents = contentRepository.findAllByThreadIdOrderBySequence(threadId);
        List<Category> categories = categoryRepository.findAllByThreadId(threadId);
        List<Tag> tags = tagRepository.findAllByThreadId(threadId);
        List<Reference> references = referenceRepository.findAllByThreadId(threadId);

        List<ContentResDto> contentResList = new ArrayList<>();
        List<CategoryResDto> categoryResList = new ArrayList<>();
        List<TagResDto> tagResList = new ArrayList<>();
        List<ReferenceResDto> referenceResList = new ArrayList<>();

        for (Content c : contents) {
            ContentResDto res = new ContentResDto();
            res.setContentId(c.getId());
            res.setType(c.getContentType());
            res.setValue(c.getValue());
            res.setSummary(c.getSummary() == null ? null : c.getSummary());

            contentResList.add(res);
        }
        for (Category c : categories) {
            CategoryResDto res = new CategoryResDto();
            res.setCategoryId(c.getId());
            res.setValue(c.getCategory());

            categoryResList.add(res);
        }
        for (Tag t : tags) {
            TagResDto res = new TagResDto();
            res.setTagId(t.getId());
            res.setValue(t.getTagName());

            tagResList.add(res);
        }
        for (Reference r : references) {
            ReferenceResDto res = new ReferenceResDto();
            res.setReferenceId(r.getId());
            res.setReferenceLink(r.getReferenceLink());
            res.setReferenceDescription(r.getReferenceDescription());

            referenceResList.add(res);
        }

        ThreadResDto resDto = new ThreadResDto();
        resDto.setCategories(categoryResList);
        resDto.setContents(contentResList);
        resDto.setReferences(referenceResList);
        resDto.setTags(tagResList);
        resDto.setId(thread.getId());
        resDto.setTitle(thread.getThreadTitle());
        resDto.setSubTitle(thread.getThreadSubTitle());
        resDto.setThumbnailUrl(thread.getThumbnailUrl());

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread Found.", resDto);
    }
}
