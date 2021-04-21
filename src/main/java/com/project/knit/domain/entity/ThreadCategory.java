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
@Table(name = "thread_category")
@Getter
@Entity
public class ThreadCategory extends TimeEntity {
    @Column(name = "thread_category_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thread_id")
    private Long threadId;

    @Column(name = "category_id")
    private Long categoryId;

    @Builder
    public ThreadCategory(Long threadId, Long categoryId) {
        this.threadId = threadId;
        this.categoryId = categoryId;
    }
}
