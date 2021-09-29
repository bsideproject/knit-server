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
@Table(name = "thread_tag")
@Getter
@Entity
public class ThreadTag extends TimeEntity {
    @Column(name = "thread_tag_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thread_id")
    private Long threadId;

    @Column(name = "tag_id")
    private Long tagId;

    @Builder
    public ThreadTag(Long threadId, Long tagId) {
        this.threadId = threadId;
        this.tagId = tagId;
    }
}
