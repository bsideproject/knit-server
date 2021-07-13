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
import javax.persistence.Table;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tag")
@Getter
@Entity
public class Tag extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_name", length = 45)
    private String tagName;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @Builder
    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public void addThread(Thread thread) {
        this.thread = thread;
    }
}
