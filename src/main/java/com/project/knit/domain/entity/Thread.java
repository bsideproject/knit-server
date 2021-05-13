package com.project.knit.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

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
    private List<Reference> referenceList = new ArrayList<>();

    private String status;

    @Builder
    public Thread(String threadTitle, String threadSubTitle, String thumbnailUrl, String threadSummary, List<Content> contentList,List<Reference> referenceList, List<Tag> tagList, List<Category> categoryList, String status) {
        this.threadTitle = threadTitle;
        this.threadSubTitle = threadSubTitle;
        this.thumbnailUrl = thumbnailUrl;
        this.threadSummary = threadSummary;
        this.contentList = contentList;
        this.referenceList = referenceList;
        this.tagList = tagList;
        this.categoryList = categoryList;
        this.status = status;
    }

    public void changeStatus(String status) {
        this.status = status;
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
