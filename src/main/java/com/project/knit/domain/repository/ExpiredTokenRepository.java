package com.project.knit.domain.repository;

import com.project.knit.domain.entity.ExpiredToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpiredTokenRepository extends JpaRepository<ExpiredToken, String> {
    List<ExpiredToken> findAllByAccessToken(String accessToken);
    List<ExpiredToken> findAllByRefreshToken(String refreshToken);
}
