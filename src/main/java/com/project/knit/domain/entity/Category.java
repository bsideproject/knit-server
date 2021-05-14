package com.project.knit.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Category extends TimeEntity {
    @Column(name = "category_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private String description;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @Builder
    public Category(String category, String description) {
        this.category = category;
        this.description = description;
    }

    public void addThread(Thread thread) {
        this.thread = thread;
    }
}
