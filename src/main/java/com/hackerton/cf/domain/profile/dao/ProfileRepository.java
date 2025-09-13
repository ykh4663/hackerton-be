package com.hackerton.cf.domain.profile.dao;

import com.hackerton.cf.domain.profile.domain.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProfileRepository extends JpaRepository<Profile,Long> {

    @Query("select p from Profile p")
    Page<Profile> findPagePlain(Pageable pageable);


    @EntityGraph(attributePaths = {"user"})
    @Query("select p from Profile p")
    Page<Profile> findPageWithUser(Pageable pageable);
}
