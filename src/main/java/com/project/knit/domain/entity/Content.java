package com.project.knit.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Entity
public class Content extends TimeEntity {
    @Column(name = "content_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contentType;

    @Column(columnDefinition = "TEXT")
    private String value;

    private String summary;

    private Integer sequence;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @Builder
    public Content(String contentType, String value, String summary, Integer sequence) {
        this.contentType = contentType.toUpperCase();
        this.value = value;
        this.summary = summary;
        this.sequence = sequence;
    }

    public void addThread(Thread thread) {
        this.thread = thread;
    }

    public void update(Content originalContent, Content newContent) {
        this.contentType = newContent.getContentType() == null ? originalContent.getContentType() : newContent.getContentType();
        this.value = newContent.getValue() == null ? originalContent.getValue() : newContent.getValue();
        this.summary = newContent.getSummary() == null ? originalContent.getSummary() : newContent.getSummary();
    }
}
