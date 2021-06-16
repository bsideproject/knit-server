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

    @Column(name = "decline_reason")
    private String declineReason;

    @JsonManagedReference
    @OneToMany(mappedBy = "thread")
    private List<Content> contents = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "thread")
    private List<Tag> tags = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "thread")
    private List<Category> categories = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "thread")
    private List<Reference> references = new ArrayList<>();

    private String status;

    private Long viewCount;

    private Long likeCount;

    @Column(name = "is_featured", columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String isFeatured;

    @Builder
    public Thread(String threadTitle, String threadSubTitle, String thumbnailUrl, String threadSummary, String status) {
        this.threadTitle = threadTitle;
        this.threadSubTitle = threadSubTitle;
        this.thumbnailUrl = thumbnailUrl;
        this.threadSummary = threadSummary;
        this.status = status;
        this.isFeatured = "N";
    }

    public void changeStatus(String status) {
        this.status = status;
    }

    public void addContents(List<Content> contents) {
        this.contents.addAll(contents);
    }

    public void addTags(List<Tag> tags) {
        this.tags.addAll(tags);
    }

    public void addCategories(List<Category> categories) {
        this.categories.addAll(categories);
    }

    public void addReferences(List<Reference> references) {
        this.references.addAll(references);
    }

    public void update(String threadSubTitle, String thumbnailUrl, String threadSummary) {
        this.threadSubTitle = threadSubTitle;
        this.thumbnailUrl = thumbnailUrl;
        this.threadSummary = threadSummary;
    }

    public void addViewCount() {
        this.viewCount += 1;
    }

    public void addLikeCount() {
        this.likeCount += 1;
    }

    public void subtractLikeCount() {
        this.likeCount -= 1;
    }

    public void feature() {
        this.isFeatured = "Y";
    }

    public void decline(String declineReason) {
        this.declineReason = declineReason;
    }

    public void updateContents(List<Content> contents) {
        this.contents.addAll(contents);
    }

    public void updateTags(List<Tag> tags) {
        this.tags.addAll(tags);
    }

    public void updateCategories(List<Category> categories) {
        this.categories.addAll(categories);
    }

    public void updateReferences(List<Reference> references) {
        this.references.addAll(references);
    }
}
