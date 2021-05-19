package com.project.knit.domain.repository;

import com.project.knit.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByThreadId(Long threadId);
    void deleteAllByThreadId(Long threadId);
}
