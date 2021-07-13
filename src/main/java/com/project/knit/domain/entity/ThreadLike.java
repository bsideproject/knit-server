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
@Table(name = "thread_like")
@Getter
@Entity
public class ThreadLike extends TimeEntity {
    @Column(name = "thread_like_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long threadId;

    private Long userId;

    @Builder
    public ThreadLike(Long threadId, Long userId) {
        this.threadId = threadId;
        this.userId = userId;
    }
}
