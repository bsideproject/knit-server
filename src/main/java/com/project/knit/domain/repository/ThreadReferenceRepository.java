package com.project.knit.domain.repository;

import com.project.knit.domain.entity.ThreadReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThreadReferenceRepository extends JpaRepository<ThreadReference, Long> {
    List<ThreadReference> findAllByThreadId(Long threadId);
}
