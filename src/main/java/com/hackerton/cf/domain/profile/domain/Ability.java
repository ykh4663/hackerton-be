package com.hackerton.cf.domain.profile.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Ability {
    @Id
    @GeneratedValue
    private Long id;

    private Integer code; // ex: 0 ~ 10

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
}