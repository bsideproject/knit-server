package com.project.knit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.knit.config.jwt.JwtTokenProvider;
import com.project.knit.domain.entity.Thread;
import com.project.knit.domain.entity.*;
import com.project.knit.domain.repository.*;
import com.project.knit.dto.req.ThreadCreateReqDto;
import com.project.knit.dto.req.ThreadLikeReqDto;
import com.project.knit.dto.req.ThreadUpdateReqDto;
import com.project.knit.dto.res.*;
import com.project.knit.utils.enums.CategoryType;
import com.project.knit.utils.enums.StatusCodeEnum;
import com.project.knit.utils.enums.ThreadStatus;
import com.project.knit.utils.enums.ThreadType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ThreadService {

    private final ThreadRepository threadRepository;
    private final ContentRepository contentRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final ReferenceRepository referenceRepository;
    private final ThreadLikeRepository threadLikeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ThreadContributorRepository threadContributorRepository;
    private final ProfileRepository profileRepository;

    public CommonResponse<ThreadShortListResDto> getThreadInfoList() {
        ThreadShortListResDto resDto = new ThreadShortListResDto();
        List<ThreadShortResDto> shortResDtos = new ArrayList<>();
        List<Thread> threads = threadRepository.findAllByStatusOrderByModifiedDateDesc(ThreadStatus.??????.name());
        threads.forEach(t -> {
            ThreadShortResDto shortResDto = new ThreadShortResDto();
            shortResDto.setId(t.getId());
            shortResDto.setTitle(t.getThreadTitle());
            shortResDto.setSubTitle(t.getThreadSubTitle());
            shortResDto.setThumbnailUrl(t.getThumbnailUrl());
            shortResDto.setLikeCount(threadLikeRepository.countAllByThreadId(t.getId()));

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
        Thread thread = threadRepository.findByIdAndStatus(id, ThreadStatus.??????.name());
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
        resDto.setThumbnailUrl(thread.getThumbnailUrl() == null ? "https://knit-document.s3.ap-northeast-2.amazonaws.com/thread/cover/cover1.png" : thread.getThumbnailUrl());
        resDto.setDate(thread.getCreatedDate());
        resDto.setIsFeatured(thread.getIsFeatured());
        List<ThreadContributor> contributors = threadContributorRepository.findAllByThreadId(threadId);
        List<String> contributorList = new ArrayList<>();
        contributors.forEach(c -> {
            Profile profile = profileRepository.findByUserId(c.getContributorUserId());
            if (profile != null) {
                contributorList.add(profile.getNickname());
            }
        });
        resDto.setContributorList(contributorList);
        resDto.setLikeCount(threadLikeRepository.countAllByThreadId(thread.getId()));

        thread.addViewCount();
        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread Found.", resDto);
    }

    @Transactional
    public <T> CommonResponse<T> registerThread(ThreadCreateReqDto threadCreateReqDto, HttpServletRequest httpServletRequest) {
        String accessToken = jwtTokenProvider.resolveToken(httpServletRequest);
        jwtTokenProvider.validateAccessToken(accessToken);
        String email = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(email);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String request = mapper.writeValueAsString(threadCreateReqDto);
            log.info("registerThread() request : {}", request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Thread thread = Thread.builder()
                .user(user)
                .threadTitle(threadCreateReqDto.getTitle())
                .threadSubTitle(threadCreateReqDto.getSubTitle())
                .coverImage(threadCreateReqDto.getCoverImage() == null ? "https://knit-document.s3.ap-northeast-2.amazonaws.com/thread/cover/knitwki_cover_default.png" : threadCreateReqDto.getCoverImage())
                .thumbnailUrl(threadCreateReqDto.getThumbnailUrl())
                .threadSummary(threadCreateReqDto.getSummary())
                .status(ThreadStatus.??????.name())
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

        // TODO TAG table??? THREAD ?????????, TAG??? ??? ????????? ??????. ??? ?????? ADMIN ACCEPT??? ??? TAG??? ?????? ??? ??????.
        List<Tag> tags = new ArrayList<>();
        threadCreateReqDto.getTags().forEach(t -> {
            List<Tag> findTags = tagRepository.findAllByTagName(t.getValue());
            if (findTags == null || findTags.isEmpty()) {
                Tag tag = Tag.builder()
                        .tagName(t.getValue())
                        .build();

                Tag createdTag = tagRepository.save(tag);
                createdTag.addThread(createdThread);
                tags.add(createdTag);
            } else {
                tags.add(findTags.get(0));
            }
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
        if (threadCreateReqDto.getReferences() != null && !threadCreateReqDto.getReferences().isEmpty()) {
            threadCreateReqDto.getReferences().forEach(r -> {
                Reference reference = Reference.builder()
                        .referenceLink(r.getReferenceLink())
                        .referenceDescription(r.getReferenceDescription())
                        .build();

                Reference createdReference = referenceRepository.save(reference);
                createdReference.addThread(createdThread);
            });
        }

        createdThread.addContents(contents);
        createdThread.addTags(tags);
        createdThread.addCategories(categories);
        createdThread.addReferences(references);

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread on the waiting list.");
    }

    @Transactional
    public <T> CommonResponse<T> updateThread(Long threadId, ThreadUpdateReqDto threadUpdateReqDto, HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.validateAccessToken(accessToken);
        String email = jwtTokenProvider.getUserPk(accessToken);
        User contributor = userRepository.findByEmail(email);

        Thread findThread = threadRepository.findByIdAndStatus(threadId, ThreadStatus.??????.name());
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

        findThread.changeStatus(ThreadStatus.??????.name());
        findThread.addContributor(contributor);

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread(Update) on the waiting list.");
    }

    public CommonResponse<ThreadListResDto> getThreadListByTagId(Long tagId, HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.validateAccessToken(accessToken);
        String email = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(email);

        Tag tag = tagRepository.findById(tagId).orElse(null);
        List<Tag> tagList = new ArrayList<>();
        tagList.add(tag);
        List<ThreadResDto> resDtoList = new ArrayList<>();

        List<Thread> threadList = threadRepository.findAllByStatusAndTagsIn("??????", tagList);
        for (Thread t : threadList) {
            ThreadResDto res = new ThreadResDto();
            res.setId(t.getId());
            res.setTitle(t.getThreadTitle());
            res.setSubTitle(t.getThreadSubTitle());
            res.setThumbnailUrl(t.getThumbnailUrl() == null ? "https://knit-document.s3.ap-northeast-2.amazonaws.com/thread/cover/cover1.png" : t.getThumbnailUrl());
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
            res.setDate(t.getCreatedDate());
            res.setIsFeatured(t.getIsFeatured());
            res.setLikeCount(threadLikeRepository.countAllByThreadId(t.getId()));

            resDtoList.add(res);

            List<ThreadContributor> contributors = threadContributorRepository.findAllByThreadId(t.getId());
            List<String> contributorList = new ArrayList<>();
            contributors.forEach(c -> {
                Profile profile = profileRepository.findByUserId(c.getContributorUserId());
                if (profile != null) {
                    contributorList.add(profile.getNickname());
                }
            });
            res.setContributorList(contributorList);
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

    public CommonResponse<ThreadMostViewedListResDto> getMostViewedList() {
        List<Thread> mostViewedThreadList = threadRepository.findTop6ByStatusOrderByViewCountDesc(ThreadStatus.??????.name());

        ThreadMostViewedListResDto resDto = new ThreadMostViewedListResDto();
        List<ThreadMostViewedResDto> mostViewedResDtoList = new ArrayList<>();
        mostViewedThreadList.forEach(t -> {
            ThreadMostViewedResDto mostViewedResDto = new ThreadMostViewedResDto();
            mostViewedResDto.setThreadId(t.getId());
            mostViewedResDto.setTitle(t.getThreadTitle());
            mostViewedResDto.setContentSummary(t.getThreadSummary());
            mostViewedResDto.setLikeCount(t.getLikeCount());
            mostViewedResDto.setViewCount(t.getViewCount());

            mostViewedResDtoList.add(mostViewedResDto);
        });
        resDto.setCount(mostViewedThreadList.size()); // should be 6
        resDto.setMostViewedThreads(mostViewedResDtoList);

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Most Viewed Threads Found.", resDto);
    }

    public CommonResponse<List<ThreadRecentChangedResDto>> getRecentChangedList() {
        List<Thread> recentChangedList = threadRepository.findTop10ByStatusOrderByModifiedDateDesc(ThreadStatus.??????.name());
        List<ThreadRecentChangedResDto> resDtos = new ArrayList<>();
        recentChangedList.forEach(t -> {
            ThreadRecentChangedResDto resDto = new ThreadRecentChangedResDto();
            resDto.setThreadId(t.getId());
            resDto.setTitle(t.getThreadTitle());
            resDto.setModifiedDate(t.getModifiedDate());

            resDtos.add(resDto);
        });

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Recent Changed Threads Found.", resDtos);
    }

    public CommonResponse<ThreadFeaturedResDto> getFeaturedThread() {
        Thread thread = threadRepository.findByStatusAndIsFeatured(ThreadStatus.??????.name(), "Y");
        ThreadFeaturedResDto resDto = new ThreadFeaturedResDto();
        resDto.setThreadId(thread.getId());
        resDto.setTitle(thread.getThreadTitle());
        resDto.setContent(thread.getContents().get(0).getValue());

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Featured Thread Found.", resDto);
    }

    public <T> CommonResponse<T> likeThread(ThreadLikeReqDto threadLikeReqDto, HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.validateAccessToken(accessToken);
        String email = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "User Not Found.");
        }

        Thread findThread = threadRepository.findByIdAndStatus(threadLikeReqDto.getThreadId(), ThreadStatus.??????.name());
        if (findThread == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "Thread Not Found.");
        }

        ThreadLike findThreadLike = threadLikeRepository.findByUserIdAndThreadId(user.getId(), findThread.getId());
        if (findThreadLike != null) {
            return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread Already Liked.");
        }

        ThreadLike threadLike = ThreadLike.builder()
                .threadId(threadLikeReqDto.getThreadId())
                .userId(user.getId())
                .build();

        threadLikeRepository.save(threadLike);
        findThread.addLikeCount();

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread Liked.");
    }

    public <T> CommonResponse<T> cancelLikeThread(ThreadLikeReqDto threadLikeReqDto, HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.validateAccessToken(accessToken);
        String email = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "User Not Found.");
        }

        Thread findThread = threadRepository.findByIdAndStatus(threadLikeReqDto.getThreadId(), ThreadStatus.??????.name());
        if (findThread == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "Thread Not Found.");
        }

        ThreadLike findThreadLike = threadLikeRepository.findByUserIdAndThreadId(user.getId(), findThread.getId());
        if (findThreadLike == null) {
            return CommonResponse.response(StatusCodeEnum.NOT_FOUND.getStatus(), "Thread Like Already Deleted.");
        }

        ThreadLike threadLike = threadLikeRepository.findByUserIdAndThreadId(user.getId(), findThread.getId());
        if (threadLike != null) {
            threadLikeRepository.delete(threadLike);
        }
        findThread.subtractLikeCount();

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread Like Cancelled.");
    }

    public CommonResponse<ThreadPagingResDto> getSearchList(String keyword, Integer page) {
        if (keyword.isBlank()) {
            return CommonResponse.response(StatusCodeEnum.OK.getStatus(), keyword + "??? ?????? ?????? ????????? ?????? ????????????.");
        }

        if (page == null) {
            page = 0;
        }

        if (keyword.contains("#")) {
            return getTagSearchList(keyword.replace("#", ""), page);
        } else {
            return getKeywordSearchList(keyword, page);
        }
    }

    public CommonResponse<ThreadPagingResDto> getKeywordSearchList(String search, Integer page) {

        search = StringEscapeUtils.unescapeJava(search);
        log.info("[TEMP] keyword : {}", search);

        List<Content> contentList = contentRepository.findAllByValueContainingOrderBySequence(search);
        List<Thread> threadList = threadRepository.findAllByThreadTitleOrContentsInOrderByModifiedDateDesc(PageRequest.of(page - 1, 10), search, contentList);

        ThreadPagingResDto resDto = new ThreadPagingResDto();
        resDto.setTotal(threadRepository.countAllByStatus(ThreadStatus.??????.name()));
        List<ThreadResDto> threadResList = new ArrayList<>();
        for (Thread t : threadList) {
            ThreadResDto res = new ThreadResDto();
            res.setId(t.getId());
            res.setTitle(t.getThreadTitle());
            res.setSubTitle(t.getThreadSubTitle());
            res.setThumbnailUrl(t.getThumbnailUrl() == null ? "https://knit-document.s3.ap-northeast-2.amazonaws.com/thread/cover/cover1.png" : t.getThumbnailUrl());
            List<ContentResDto> contentResList = new ArrayList<>();
            List<Content> contents = contentRepository.findAllByThreadIdOrderBySequence(t.getId());
            contents.forEach(c -> {
                ContentResDto contentRes = new ContentResDto();
                contentRes.setContentId(c.getId());
                contentRes.setType(c.getContentType());
                contentRes.setValue(c.getValue());
                contentRes.setSummary(c.getSummary());

                contentResList.add(contentRes);
            });
            res.setContents(contentResList);
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
            res.setDate(t.getCreatedDate());
            res.setIsFeatured(t.getIsFeatured());
            List<ThreadContributor> contributors = threadContributorRepository.findAllByThreadId(t.getId());
            List<String> contributorList = new ArrayList<>();
            contributors.forEach(c -> {
                Profile profile = profileRepository.findByUserId(c.getContributorUserId());
                if (profile != null) {
                    contributorList.add(profile.getNickname());
                }
            });
            res.setContributorList(contributorList);
            res.setLikeCount(threadLikeRepository.countAllByThreadId(t.getId()));

            threadResList.add(res);
        }
        resDto.setThreads(threadResList);
        int total = threadRepository.countAllByThreadTitleOrContentsIn(search, contentList);
        resDto.setTotal(total);
        if (!threadResList.isEmpty()) {
            if ((page) * 10 > total) {
                resDto.setNextPage(null);
            } else {
                resDto.setNextPage(page + 1);
            }
        } else {
            resDto.setNextPage(null);
        }

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Keyword Search List.", resDto);
    }

    public CommonResponse<ThreadPagingResDto> getTagSearchList(String tag, Integer page) {

        log.info("[TEMP] keyword-tag : {}", tag);
        List<Tag> tags = tagRepository.findAllByTagName(tag);
        List<Tag> distinctTags = tags.stream().distinct().collect(Collectors.toList());

        List<Category> categories = categoryRepository.findAllByCategory(tag);
        List<Category> distinctCategories = categories.stream().distinct().collect(Collectors.toList());

        if (page == null) {
            page = 1;
        }

        // ?????? ?????? ??????
        List<Thread> threadList = threadRepository.findAllByTagsInOrCategoriesIn(PageRequest.of(page - 1, 10, Sort.by("modifiedDate").descending()), distinctTags, distinctCategories);

        ThreadPagingResDto resDto = new ThreadPagingResDto();
        List<ThreadResDto> threadResList = new ArrayList<>();
        for (Thread t : threadList) {
            ThreadResDto res = new ThreadResDto();
            res.setId(t.getId());
            res.setTitle(t.getThreadTitle());
            res.setSubTitle(t.getThreadSubTitle());
            res.setThumbnailUrl(t.getThumbnailUrl() == null ? "https://knit-document.s3.ap-northeast-2.amazonaws.com/thread/cover/cover1.png" : t.getThumbnailUrl());
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
            res.setDate(t.getCreatedDate());
            res.setIsFeatured(t.getIsFeatured());
            List<ThreadContributor> contributors = threadContributorRepository.findAllByThreadId(t.getId());
            List<String> contributorList = new ArrayList<>();
            contributors.forEach(c -> {
                Profile profile = profileRepository.findByUserId(c.getContributorUserId());
                if (profile != null) {
                    contributorList.add(profile.getNickname());
                }
            });
            res.setContributorList(contributorList);
            res.setLikeCount(threadLikeRepository.countAllByThreadId(t.getId()));

            threadResList.add(res);
        }
        resDto.setThreads(threadResList);
        int total = threadRepository.countAllByTagsInOrCategoriesIn(tags, categories);
        resDto.setTotal(total);
        if (!threadResList.isEmpty()) {
            if ((page) * 10 > total) {
                resDto.setNextPage(null);
            } else {
                resDto.setNextPage(page + 1);
            }
        } else {
            resDto.setNextPage(null);
        }

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Tag Search List.", resDto);
    }

    public CommonResponse<ThreadPagingResDto> getCollectionList(String type, Integer page) {
        if (page == null) {
            page = 1;
        }

        if (type == null) {
            type = ThreadType.THREAD.getType();
        }

        // todo ?????? ??????/?????? ??? ?????? ??????
//        if(type.equals())

        List<Thread> threadList = threadRepository.findAllByStatusOrderByModifiedDateDesc(PageRequest.of(page - 1, 10, Sort.by("modifiedDate").descending()), ThreadStatus.??????.name());

        ThreadPagingResDto resDto = new ThreadPagingResDto();
        List<ThreadResDto> threadResList = new ArrayList<>();
        for (Thread t : threadList) {
            ThreadResDto res = new ThreadResDto();
            res.setId(t.getId());
            res.setTitle(t.getThreadTitle());
            res.setSubTitle(t.getThreadSubTitle());
            res.setThumbnailUrl(t.getThumbnailUrl() == null ? "https://knit-document.s3.ap-northeast-2.amazonaws.com/thread/cover/cover1.png" : t.getThumbnailUrl());
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
            res.setDate(t.getCreatedDate());
            res.setIsFeatured(t.getIsFeatured());
            List<ThreadContributor> contributors = threadContributorRepository.findAllByThreadId(t.getId());
            List<String> contributorList = new ArrayList<>();
            contributors.forEach(c -> {
                Profile profile = profileRepository.findByUserId(c.getContributorUserId());
                if (profile != null) {
                    contributorList.add(profile.getNickname());
                }
            });
            res.setContributorList(contributorList);
            res.setLikeCount(threadLikeRepository.countAllByThreadId(t.getId()));

            threadResList.add(res);
        }
        resDto.setThreads(threadResList);

        int total = threadRepository.countAllByStatus(ThreadStatus.??????.name());
        resDto.setTotal(total);
        if (!threadResList.isEmpty()) {
            if ((page) * 10 > total) {
                resDto.setNextPage(null);
            } else {
                resDto.setNextPage(page + 1);
            }
        } else {
            resDto.setNextPage(null);
        }

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread Collection List.", resDto);
    }

    public CommonResponse<ThreadPagingResDto> getGroupList(String category, String type, Integer page) {
        if (page == null) {
            page = 1;
        }
        if (category == null) {
            category = CategoryType.PLANNING.getGroupEn();
        }
        if (type == null) {
            type = ThreadType.THREAD.getType();
        }

        ThreadPagingResDto resDto = new ThreadPagingResDto();
        // todo ?????? ??????/?????? ??? ?????? ??????
        if (type.equals(ThreadType.THREAD.getType())) {
            List<Category> categories = categoryRepository.findAllByCategory(CategoryType.valueOf(category.toUpperCase()).getGroupEn());
            List<Thread> threadList = threadRepository.findAllByStatusAndCategoriesInOrderByModifiedDateDesc(PageRequest.of(page - 1, 10, Sort.by("modifiedDate").descending()), ThreadStatus.??????.name(), categories);

            List<ThreadResDto> threadResList = new ArrayList<>();
            for (Thread t : threadList) {
                ThreadResDto res = new ThreadResDto();
                res.setId(t.getId());
                res.setTitle(t.getThreadTitle());
                res.setSubTitle(t.getThreadSubTitle());
                res.setThumbnailUrl(t.getThumbnailUrl() == null ? "https://knit-document.s3.ap-northeast-2.amazonaws.com/thread/cover/cover1.png" : t.getThumbnailUrl());
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
                res.setDate(t.getCreatedDate());
                res.setIsFeatured(t.getIsFeatured());
                List<ThreadContributor> contributors = threadContributorRepository.findAllByThreadId(t.getId());
                List<String> contributorList = new ArrayList<>();
                contributors.forEach(c -> {
                    Profile profile = profileRepository.findByUserId(c.getContributorUserId());
                    if (profile != null) {
                        contributorList.add(profile.getNickname());
                    }
                });
                res.setContributorList(contributorList);
                res.setLikeCount(threadLikeRepository.countAllByThreadId(t.getId()));

                threadResList.add(res);
            }
            resDto.setThreads(threadResList);

            int total = threadRepository.countAllByStatus(ThreadStatus.??????.name());
            resDto.setTotal(total);
            if (!threadResList.isEmpty()) {
                if ((page) * 10 > total) {
                    resDto.setNextPage(null);
                } else {
                    resDto.setNextPage(page + 1);
                }
            } else {
                resDto.setNextPage(null);
            }
        }

        return CommonResponse.response(StatusCodeEnum.OK.getStatus(), "Thread Per Category/Type List.", resDto);
    }
}
