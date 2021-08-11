package com.project.knit.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
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

    @Email
    @Column(length = 75)
    private String email;

    @Column(columnDefinition = "LONGTEXT")
    private String password;

    @Column(columnDefinition = "VARCHAR(10) COMMENT 'GOOGLE || NAVER' ")
    private String type;

    @Column(columnDefinition = "LONGTEXT")
    private String token;

    @Column(columnDefinition = "LONGTEXT")
    private String refreshToken;

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

    public void addRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
