package com.hackerton.cf.domain.profile.domain;



import com.hackerton.cf.domain.user.domain.User;
import com.hackerton.cf.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profile")
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class Profile extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY)
    private User user;



    @Enumerated(EnumType.STRING)
    private CareerStatus careerStatus;

    /** 위에서 정의한 프로필 기본 정보 */
    @Embedded
    private ProfileBasic profileBasic;

    /** 능력 선택(0~10 중 하나) */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Ability> abilities = new ArrayList<>();



    //=== 편의 메서드 ===//

    public void updateBasic(ProfileBasic newBasic) {
        this.profileBasic = newBasic;
    }


    public void updateCareerStatus(CareerStatus careerStatus) {
        this.careerStatus = careerStatus;
    }



}