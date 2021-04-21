package com.project.knit.domain.repository;

import com.project.knit.domain.entity.ThreadCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadCategoryRepository extends JpaRepository<ThreadCategory, Long> {
}
