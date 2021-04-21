package com.project.knit.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.knit.dto.req.ThreadUpdateReqDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Thread extends TimeEntity {

    @Column(name = "thread_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thread_title", updatable = false)
    private String threadTitle;

    @Column(name = "thread_sub_title")
    private String threadSubTitle;

    @Column(name = "thread_thumbnail")
    private String threadThumbnail;

    @Column(name = "thread_summary")
    private String threadSummary;

    @JsonManagedReference
    @OneToMany(mappedBy = "thread")
    private List<Content> contentList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "thread")
    private List<Tag> tagList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "thread")
    private List<Category> categoryList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "thread")
    private List<ThreadReference> threadReferenceList = new ArrayList<>();

    private String status;

    @Builder
    public Thread(String threadTitle, String threadSubTitle, String threadThumbnail, String threadSummary, List<Content> contentList, List<ThreadReference> threadReferenceList, List<Tag> tagList, List<Category> categoryList, String status) {
        this.threadTitle = threadTitle;
        this.threadSubTitle = threadSubTitle;
        this.threadThumbnail = threadThumbnail;
        this.threadSummary = threadSummary;
        this.contentList = contentList;
        this.threadReferenceList = threadReferenceList;
        this.tagList = tagList;
        this.categoryList = categoryList;
        this.status = status;
    }

    public void changeStatus(String status) {
        this.status = status;
    }

    public void update(ThreadUpdateReqDto updateReqDto, Thread thread) {
        this.threadSubTitle = updateReqDto.getSubTitle() == null ? thread.getThreadSubTitle() : updateReqDto.getSubTitle();
        this.threadThumbnail = updateReqDto.getThumbnail() == null ? thread.getThreadThumbnail() : updateReqDto.getThumbnail();
        this.threadSummary = updateReqDto.getSummary() == null ? thread.getThreadSummary() : updateReqDto.getSummary();
        this.contentList = updateReqDto.getContentList() == null ? thread.getContentList() : updateReqDto.getContentList();
        this.threadReferenceList = updateReqDto.getThreadReferenceList() == null ? thread.getThreadReferenceList() : updateReqDto.getThreadReferenceList();
        this.tagList = updateReqDto.getTagList() == null ? thread.getTagList() : updateReqDto.getTagList();
        this.categoryList = updateReqDto.getCategoryList() == null ?thread.getCategoryList() : updateReqDto.getCategoryList();
    }

    public void addContent(Content content) {
        this.contentList.add(content);
    }

    public void addTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public void addCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }
}
