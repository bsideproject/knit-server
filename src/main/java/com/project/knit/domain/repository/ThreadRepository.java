package com.project.knit.domain.repository;

import com.project.knit.domain.entity.Tag;
import com.project.knit.domain.entity.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThreadRepository extends JpaRepository<Thread, Long> {
    List<Thread> findAllByStatusAndTagsIn(String status, List<Tag> tags);

    List<Thread> findAllByStatusOrderByModifiedDateDesc(String status);

    Thread findByIdAndStatus(Long id, String status);

    List<Thread> findTop6ByStatusOrderByViewCountDesc(String status);

    List<Thread> findTop10ByStatusOrderByModifiedDateDesc(String status);

    Thread findByStatusAndIsFeatured(String status, String isFeatured);
}
