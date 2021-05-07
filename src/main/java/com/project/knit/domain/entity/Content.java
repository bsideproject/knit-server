package com.project.knit.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.knit.utils.enums.ThreadType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Entity
public class Content extends TimeEntity {
    @Column(name = "content_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String threadType;

    private String value;

    private String summary;

    @JsonIgnore
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @Builder
    public Content(String threadType, String value, Thread thread) {
        this.threadType = threadType.toUpperCase();
        this.value = value;
        this.thread = thread;
    }

    public void addThread(Thread thread) {
        this.thread = thread;
    }

    public void update(Content originalContent, Content newContent) {
        this.threadType = newContent.getThreadType() == null ? originalContent.getThreadType() : newContent.getThreadType();
        this.value = newContent.getValue() == null ? originalContent.getValue() : newContent.getValue();
        this.summary = newContent.getSummary() == null ? originalContent.getSummary() : newContent.getSummary();
    }
}
