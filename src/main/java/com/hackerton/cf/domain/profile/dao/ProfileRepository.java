package com.hackerton.cf.domain.profile.dao;

import com.hackerton.cf.domain.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile,Long> {
}
