package com.project.knit.domain.repository;

import com.project.knit.domain.entity.Reference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceRepository extends JpaRepository<Reference, Long> {
    List<Reference> findAllByThreadId(Long threadId);
    void deleteAllByThreadId(Long threadId);
}
