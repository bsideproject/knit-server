package com.project.knit.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "reference")
@Getter
@Entity
public class ThreadReference extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_link", columnDefinition = "VARCHAR(500)", nullable = false)
    private String referenceLink;

    @Column(name = "reference_description", columnDefinition = "VARCHAR(500) COMMENT '참조링크에 대한 설명'")
    private String referenceDescription;

    @JsonIgnore
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @Builder
    public ThreadReference(String referenceLink, String referenceDescription, Thread thread) {
        this.referenceLink = referenceLink;
        this.referenceDescription = referenceDescription;
        this.thread = thread;
    }

    public void addThread(Thread thread) {
        this.thread = thread;
    }
}
