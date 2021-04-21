package com.project.knit.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.knit.utils.enums.ThreadType;
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

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ThreadType threadType;

    private String value;

    private String summary;

    @JsonIgnore
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @Builder
    public Content(ThreadType threadType, String value, Thread thread) {
        this.threadType = threadType;
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
