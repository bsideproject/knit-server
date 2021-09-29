package com.project.knit.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "thread_contributor")
@Getter
@Entity
public class ThreadContributor extends TimeEntity {
    @Column(name = "thread_contributor_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long threadId;

    private String threadTitle;

    private String threadType;

    @Column(name = "contributor_user_id")
    private Long contributorUserId;

    @Builder
    public ThreadContributor(Long threadId, String threadTitle, String threadType, Long contributorUserId) {
        this.threadId = threadId;
        this.threadTitle = threadTitle;
        this.threadType = threadType;
        this.contributorUserId = contributorUserId;
    }
}
