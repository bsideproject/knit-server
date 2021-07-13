package com.project.knit.domain.entity;

import lombok.AccessLevel;
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

    @Column(columnDefinition = "VARCHAR(100) COMMENT 'Github 주소 입력'")
    private String github;

    @Column(columnDefinition = "VARCHAR(100) COMMENT '링크드인 주소 입력'")
    private String linkedin;

    @Column(name = "profile_image")
    private String profileImage;

    private String introduction;
}
