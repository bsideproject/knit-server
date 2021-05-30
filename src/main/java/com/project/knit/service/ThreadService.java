package com.project.knit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.knit.domain.entity.Category;
import com.project.knit.domain.entity.Content;
import com.project.knit.domain.entity.Reference;
import com.project.knit.domain.entity.Tag;
import com.project.knit.domain.entity.Thread;
import com.project.knit.domain.repository.CategoryRepository;
import com.project.knit.domain.repository.ContentRepository;
import com.project.knit.domain.repository.ReferenceRepository;
import com.project.knit.domain.repository.TagRepository;
import com.project.knit.domain.repository.ThreadRepository;
import com.project.knit.dto.req.ThreadCreateReqDto;
import com.project.knit.dto.req.ThreadUpdateReqDto;
import com.project.knit.dto.res.*;
import com.project.knit.utils.enums.StatusCodeEnum;
import com.project.knit.utils.enums.ThreadStatus;
import com.project.knit.utils.enums.ThreadType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ThreadService {

    private final ThreadRepository threadRepository;
    private final ContentRepository contentRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final ReferenceRepository referenceRepository;

    private final S3Service s3Service;
    private final AdminService adminService;

    public CommonResponse<ThreadShortListResDto> getThreadInfoList() {
        ThreadShortListResDto resDto = new ThreadShortListResDto();
        List<ThreadShortResDto> shortResDtos = new ArrayList<>();
        List<Thread> threads = threadRepository.findAllByStatusOrderByModifiedDateDesc(ThreadStatus.승인.name());
        threads.forEach(t -> {
            ThreadShortResDto shortResDto = new ThreadShortResDto();
            shortResDto.setId(t.getId());
            shortResDto.setTitle(t.getThreadTitle());
            shortResDto.setSubTitle(t.getThreadSubTitle());
            shortResDto.setThumbnailUrl(t.getThumbnailUrl());

            shortResDtos.add(shortResDto);
        });
        resDto.setCount(threads.size());
        resDto.setThreads(shortResDtos);

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread List Found.", resDto);
    }

    public <T> CommonResponse<T> checkTagName(String tagName) {
        Tag tag = tagRepository.findByTagName(tagName);
        if (tag != null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "Tag Name Already Exists.");
        } else {
            return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Available Tag Name.");
        }
    }

    @Transactional
    public CommonResponse<ThreadResDto> getThreadInfoById(Long id) {
        Thread thread = threadRepository.findByIdAndStatus(id, ThreadStatus.승인.name());
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

    @Transactional
    public <T> CommonResponse<T> registerThread(ThreadCreateReqDto threadCreateReqDto) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String request = mapper.writeValueAsString(threadCreateReqDto);
            log.info("registerThread() request : {}", request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
//        if (!threadCreateReqDto.getTags().isEmpty()) {
//            threadCreateReqDto.getTags().forEach(t -> {
//                checkTagName(t.getValue());
//            });
//        }

        Thread thread = Thread.builder()
                .threadTitle(threadCreateReqDto.getTitle())
                .threadSubTitle(threadCreateReqDto.getSubTitle())
                .thumbnailUrl(threadCreateReqDto.getThumbnailUrl())
                .threadSummary(threadCreateReqDto.getSummary())
                .status(ThreadStatus.대기.name())
                .build();

        Thread createdThread = threadRepository.save(thread);

        List<Content> contents = new ArrayList<>();
        threadCreateReqDto.getContents().forEach(c -> {
            Content content = Content.builder()
                    .contentType(c.getType())
                    .value(c.getValue())
                    .summary(c.getSummary() == null ? null : c.getSummary())
                    .sequence(c.getSequence())
                    .build();

            Content createdContent = contentRepository.save(content);
            createdContent.addThread(createdThread);
            contents.add(createdContent);
        });

        List<Tag> tags = new ArrayList<>();
        threadCreateReqDto.getTags().forEach(t -> {
            Tag tag = Tag.builder()
                    .tagName(t.getValue())
                    .build();

            Tag createdTag = tagRepository.save(tag);
            createdTag.addThread(createdThread);
            tags.add(createdTag);
        });

        List<Category> categories = new ArrayList<>();
        threadCreateReqDto.getCategories().forEach(c -> {
            Category category = Category.builder()
                    .category(c.getValue())
                    .build();

            Category createdCategory = categoryRepository.save(category);
            createdCategory.addThread(createdThread);
            categories.add(createdCategory);
        });

        List<Reference> references = new ArrayList<>();
        threadCreateReqDto.getReferences().forEach(r -> {
            Reference reference = Reference.builder()
                    .referenceLink(r.getReferenceLink())
                    .referenceDescription(r.getReferenceDescription())
                    .build();

            Reference createdReference = referenceRepository.save(reference);
            createdReference.addThread(createdThread);
        });

        createdThread.addContents(contents);
        createdThread.addTags(tags);
        createdThread.addCategories(categories);
        createdThread.addReferences(references);

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread on the waiting list.");
    }

    @Transactional
    public <T> CommonResponse<T> updateThread(Long threadId, ThreadUpdateReqDto threadUpdateReqDto) {
        Thread findThread = threadRepository.findByIdAndStatus(threadId, ThreadStatus.승인.name());
        if (findThread == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "Thread to update Not Found.");
        }

        findThread.update(threadUpdateReqDto.getSubTitle(), threadUpdateReqDto.getThumbnailUrl(), threadUpdateReqDto.getSummary());

        Long findThreadId = findThread.getId();
        if (threadUpdateReqDto.getContents() != null) {
            contentRepository.deleteAllByThreadId(findThread.getId());
            List<Content> contents = new ArrayList<>();
            threadUpdateReqDto.getContents().forEach(c -> {
                Content content = Content.builder()
                        .contentType(c.getType())
                        .value(c.getValue())
                        .summary(c.getSummary() == null ? null : c.getSummary())
                        .build();

                Content createdContent = contentRepository.save(content);
                createdContent.addThread(findThread);
                contents.add(createdContent);
            });
            contentRepository.saveAll(contents);
            findThread.updateContents(contents);
        }

        if (threadUpdateReqDto.getTags() != null) {
            tagRepository.deleteAllByThreadId(findThreadId);
            List<Tag> tags = new ArrayList<>();
            threadUpdateReqDto.getTags().forEach(t -> {
                Tag tag = Tag.builder()
                        .tagName(t.getValue())
                        .build();

                Tag createdTag = tagRepository.save(tag);
                createdTag.addThread(findThread);
                tags.add(createdTag);
            });
            tagRepository.saveAll(tags);
            findThread.updateTags(tags);
        }

        if (threadUpdateReqDto.getCategories() != null) {
            categoryRepository.deleteAllByThreadId(findThreadId);
            List<Category> categories = new ArrayList<>();
            threadUpdateReqDto.getCategories().forEach(c -> {
                Category category = Category.builder()
                        .category(c.getValue())
                        .build();

                Category createdCategory = categoryRepository.save(category);
                createdCategory.addThread(findThread);
                categories.add(createdCategory);
            });
            categoryRepository.saveAll(categories);
            findThread.updateCategories(categories);
        }

        if (threadUpdateReqDto.getReferences() != null) {
            referenceRepository.deleteAllByThreadId(findThreadId);
            List<Reference> references = new ArrayList<>();
            threadUpdateReqDto.getReferences().forEach(r -> {
                Reference reference = Reference.builder()
                        .referenceLink(r.getReferenceLink())
                        .referenceDescription(r.getReferenceDescription())
                        .build();

                Reference createdReference = referenceRepository.save(reference);
                createdReference.addThread(findThread);
            });
            referenceRepository.saveAll(references);
            findThread.updateReferences(references);
        }

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread(Update) on the waiting list.");
    }


    public CommonResponse<ThreadListResDto> getThreadListByTagId(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElse(null);
        List<Tag> tagList = new ArrayList<>();
        tagList.add(tag);
        List<ThreadResDto> resDtoList = new ArrayList<>();

        List<Thread> threadList = threadRepository.findAllByStatusAndTagsIn("승인", tagList);
        for (Thread t : threadList) {
            ThreadResDto res = new ThreadResDto();
            res.setId(t.getId());
            res.setTitle(t.getThreadTitle());
            res.setSubTitle(t.getThreadSubTitle());
            res.setThumbnailUrl(t.getThumbnailUrl());
            List<ContentResDto> contentList = new ArrayList<>();
            List<Content> contents = contentRepository.findAllByThreadIdOrderBySequence(t.getId());
            contents.forEach(c -> {
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
            List<TagResDto> tagResList = new ArrayList<>();
            t.getTags().forEach(tr -> {
                TagResDto tagRes = new TagResDto();
                tagRes.setTagId(tr.getId());
                tagRes.setValue(tr.getTagName());

                tagResList.add(tagRes);
            });
            res.setTags(tagResList);
            List<ReferenceResDto> referenceList = new ArrayList<>();
            t.getReferences().forEach(r -> {
                ReferenceResDto referenceRes = new ReferenceResDto();
                referenceRes.setReferenceId(r.getId());
                referenceRes.setReferenceLink(r.getReferenceLink());
                referenceRes.setReferenceDescription(r.getReferenceDescription());

                referenceList.add(referenceRes);
            });
            res.setReferences(referenceList);

            resDtoList.add(res);
        }
        ThreadListResDto res = new ThreadListResDto();
        res.setCount(resDtoList.size());
        res.setThreads(resDtoList);

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread List by Tag ID Found.", res);
    }

    public CommonResponse<List<TagResDto>> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        List<TagResDto> resDtos = new ArrayList<>();
        tags.forEach(tag -> {
            TagResDto res = new TagResDto();
            res.setTagId(tag.getId());
            res.setValue(tag.getTagName());

            resDtos.add(res);
        });
        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "All Tags Found.", resDtos);
    }

    public CommonResponse<List<CategoryResDto>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResDto> resDtos = new ArrayList<>();
        categories.forEach(c -> {
            CategoryResDto res = new CategoryResDto();
            res.setCategoryId(c.getId());
            res.setValue(c.getCategory());

            resDtos.add(res);
        });

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "All Tags Found.", resDtos);
    }
}
