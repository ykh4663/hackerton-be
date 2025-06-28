package com.hackerton.cf.domain.user.domain;


import com.hackerton.cf.domain.profile.domain.Profile;
import jakarta.persistence.*;
import lombok.*;


@Table(name = "USERS")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "OAUTH_ID",nullable = false)
    private String oauthId;

    @Column(nullable = true)
    private String nickname;

    @Column(name = "role")
    private String role;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;


    public void updateRole(String newRole) {
        this.role = newRole;
    }

}