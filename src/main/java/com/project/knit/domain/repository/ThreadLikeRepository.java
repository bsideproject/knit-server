package com.project.knit.domain.repository;

import com.project.knit.domain.entity.ThreadLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadLikeRepository extends JpaRepository<ThreadLike, Long> {
}
