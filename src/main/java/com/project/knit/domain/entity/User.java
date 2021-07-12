package com.project.knit.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
@Getter
@Entity
public class User extends TimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45)
    private String nickname;

    @Email
    @Column(length = 75)
    private String email;

    @Column(length = 75)
    private String password;

    @Column(columnDefinition = "VARCHAR(100) COMMENT 'Github 주소 입력'")
    private String github;

    @Column(columnDefinition = "VARCHAR(100) COMMENT '링크드인 주소 입력'")
    private String linkedin;

    @Column(name = "profile_image")
    private String profileImage;

    private String introduction;

    @Column(columnDefinition = "VARCHAR(10) COMMENT 'GOOGLE || NAVER' ")
    private String type;

    private String token;
    private String accessToken;

    @Column(nullable = false)
    private String role;

    @Builder
    public User(String email, String role, String password, String type, String token) {
        this.email = email;
        this.role = role;
        this.password = password;
        this.type = type;
        this.token = token;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public void addAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
