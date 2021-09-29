package com.project.knit.domain.repository;

import com.project.knit.domain.entity.ThreadContributor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThreadContributorRepository extends JpaRepository<ThreadContributor, Long> {
    List<ThreadContributor> findAllByThreadId(Long threadId);

    List<ThreadContributor> findAllByContributorUserId(Long userId);
}
