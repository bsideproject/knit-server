package com.project.knit.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "profile")
@Entity
public class Profile extends TimeEntity {
    @Column(name = "profile_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String email;

    private String nickname;

    @Column(columnDefinition = "VARCHAR(100) COMMENT 'Github 주소 입력'")
    private String github;

    @Column(columnDefinition = "VARCHAR(100) COMMENT '링크드인 주소 입력'")
    private String linkedIn;

    private String introduction;

    @Column(name = "profile_image")
    private String profileImage;

    @Builder
    public Profile(User user, String email, String nickname) {
        this.user = user;
        this.email = email;
        this.nickname = nickname;
    }

    public void updateProfile(String nickname, String github, String linkedIn, String introduction) {
        this.nickname = nickname;
        this.github = github;
        this.linkedIn = linkedIn;
        this.introduction = introduction;
    }
}
