package com.project.knit.domain.repository;

import com.project.knit.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByTagName(String tagName);
    List<Tag> findAllByThreadId(Long threadId);
}
