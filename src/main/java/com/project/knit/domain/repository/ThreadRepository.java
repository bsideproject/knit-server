package com.project.knit.domain.repository;

import com.project.knit.domain.entity.Content;
import com.project.knit.domain.entity.Tag;
import com.project.knit.domain.entity.Thread;
import com.project.knit.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThreadRepository extends JpaRepository<Thread, Long> {
    List<Thread> findAllByStatusAndTagsIn(String status, List<Tag> tags);

    List<Thread> findAllByStatusOrderByModifiedDateDesc(String status);

    Thread findByIdAndStatus(Long id, String status);

    List<Thread> findTop6ByStatusOrderByViewCountDesc(String status);

    List<Thread> findTop10ByStatusOrderByModifiedDateDesc(String status);

    Thread findByStatusAndIsFeatured(String status, String isFeatured);

    List<Thread> findAllByThreadTitleOrContentsInOrderByModifiedDateDesc(Pageable pageable, String keyword, List<Content> contentList);

    Integer countAllByStatus(String status);

    Integer countAllByTagsInAndStatus(List<Tag> tagList, String status);

    List<Thread> findAllByTagsIn(Pageable pageable, List<Tag> tagList);

    Thread findByIsFeatured(String isFeatured);

    List<Thread> findAllByUserAndStatusOrderByCreatedDateDesc(User user, String status);
}
