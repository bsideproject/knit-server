package com.project.knit.domain.repository;

import com.project.knit.domain.entity.Profile;
import com.project.knit.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByUser(User user);

    Profile findByUserId(Long userId);
}
