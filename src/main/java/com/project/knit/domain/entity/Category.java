package com.project.knit.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Category extends TimeEntity {
    @Column(name = "category_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @Builder
    public Category(String category) {
        this.category = category;
    }

    public void addThread(Thread thread) {
        this.thread = thread;
    }
}
