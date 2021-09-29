package com.project.knit.domain.repository;

import com.project.knit.domain.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    UserToken findByRefreshToken(String refreshToken);

    List<UserToken> findAllByUserId(Long userId);

    List<UserToken> findAllByRefreshToken(String refreshToken);
}
