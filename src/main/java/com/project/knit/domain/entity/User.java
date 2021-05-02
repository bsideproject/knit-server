package com.project.knit.domain.entity;

import com.project.knit.utils.enums.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
@Getter
@Entity
public class User extends TimeEntity {

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

    @Column(columnDefinition = "VARCHAR(100) DEFAULT 'Github 주소 입력'")
    private String github;

    @Column(columnDefinition = "VARCHAR(100) DEFAULT '링크드인 주소 입력'")
    private String linkedin;

    private String image;

    // default 255
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public User(String email, Role role) {
        this.email = email;
        this.role = role;
    }
}
