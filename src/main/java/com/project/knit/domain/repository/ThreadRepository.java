package com.project.knit.domain.repository;

import com.project.knit.domain.entity.Tag;
import com.project.knit.domain.entity.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThreadRepository extends JpaRepository<Thread, Long> {
    List<Thread> findAllByStatusAndTagListIn(String status, List<Tag> tagList);
    List<Thread> findAllByStatusOrderByModifiedDateDesc(String status);
    Thread findByIdAndStatus(Long id, String status);
}
