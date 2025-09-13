package com.hackerton.cf.domain.profile.service;

import com.hackerton.cf.domain.auth.dto.RoleStatus;
import com.hackerton.cf.domain.profile.dao.ProfileRepository;

import com.hackerton.cf.domain.profile.domain.*;
import com.hackerton.cf.domain.profile.dto.ProfileDto;
import com.hackerton.cf.domain.profile.dto.ProfileRequest;
import com.hackerton.cf.domain.profile.dto.ProfileResponse;
import com.hackerton.cf.domain.user.dao.UserRepository;
import com.hackerton.cf.domain.user.domain.User;
import com.hackerton.cf.domain.user.service.UserService;
import com.hackerton.cf.global.error.ApplicationException;
import com.hackerton.cf.global.error.ProfileErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final UserService userService;


    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User user = userService.getUserById(userId);


        Profile p = user.getProfile();
        if (p == null) {
            throw new ApplicationException(ProfileErrorCode.NOT_FOUND_PROFILE);
        }
        ProfileResponse resp = new ProfileResponse();


        ProfileBasic profileBasic = p.getProfileBasic();
        ProfileDto basicDto = new ProfileDto(
                profileBasic.getUniversityYear(),
                profileBasic.getDepartment(),
                profileBasic.getCertifications(),
                profileBasic.getLanguages(),
                profileBasic.getAwards(),
                profileBasic.getExperienceText()
        );
        resp.setBasic(basicDto);

        List<Integer> abilities = p.getAbilities().stream()
                .map(Ability::getCode)
                .toList();
        resp.setAbilities(abilities);


        resp.setCareerCode(resp.getCareerCode());
        return resp;
    }


    public void upsertProfile(Long userId, ProfileRequest req) {
        User user = userService.getUserById(userId);

        if (req.getNickname() != null) {
            user.setNickname(req.getNickname());
        }

        final Profile profile = Optional.ofNullable(user.getProfile())
                .orElseGet(() -> {
                    Profile p = Profile.builder().build();
                    user.setProfile(p);
                    return p;
                });

        // 기본 정보 업데이트
        ProfileDto b = req.getBasic();
        ProfileBasic basic = ProfileBasic.builder()
                .department(b.getDepartment())
                .universityYear(b.getUniversityYear())
                .certifications(b.getCertifications())
                .languages(b.getLanguages())
                .awards(b.getAwards())
                .experienceText(b.getExperienceText())
                .build();
        profile.updateBasic(basic);
        CareerStatus status = switch (req.getCareerCode()) {
            case 0 -> CareerStatus.신입;
            case 1 -> CareerStatus.경력;
            default -> throw new ApplicationException(ProfileErrorCode.INVALID_CAREER_CODE);
        };

        profile.updateCareerStatus(status);
        // === Ability 리스트 재설정 === //
        // 기존 abilities 제거
        profile.getAbilities().clear();

        // 새로운 abilities 추가
        List<Ability> abilityEntities = req.getAbilities().stream()
                .map(code -> {
                    Ability ability = new Ability();
                    ability.setCode(code);
                    ability.setProfile(profile);  // 양방향 설정
                    return ability;
                })
                .toList();
        profile.getAbilities().addAll(abilityEntities);

        profileRepository.save(profile);

        // 회원 상태 변경
        if (RoleStatus.REGISTER.getStatus().equals(user.getRole())) {
            user.updateRole(RoleStatus.COMPLETE.getStatus());
            userRepository.save(user);
        }
    }

    public ProfileResponse mergeProfile(ProfileResponse base, ProfileRequest override) {
        ProfileResponse result = new ProfileResponse();



        result.setAbilities(
                override.getAbilities() != null && !override.getAbilities().isEmpty()
                        ? override.getAbilities()
                        : base.getAbilities()
        );

        result.setCareerCode(
                override.getCareerCode() != null
                        ? (override.getCareerCode() == 0 ? "신입" : "경력")
                        : base.getCareerCode()
        );

        ProfileDto baseBasic = base.getBasic();
        ProfileDto overrideBasic = override.getBasic();

        result.setBasic(
                overrideBasic != null &&
                        (overrideBasic.getUniversityYear() != null || overrideBasic.getDepartment() != null)
                        ? new ProfileDto(
                        overrideBasic.getUniversityYear() != null ? overrideBasic.getUniversityYear() : baseBasic.getUniversityYear(),
                        overrideBasic.getDepartment() != null ? overrideBasic.getDepartment() : baseBasic.getDepartment(),
                        overrideBasic.getCertifications() != null ? overrideBasic.getCertifications() : baseBasic.getCertifications(),
                        overrideBasic.getLanguages() != null ? overrideBasic.getLanguages() : baseBasic.getLanguages(),
                        overrideBasic.getAwards() != null ? overrideBasic.getAwards() : baseBasic.getAwards(),
                        overrideBasic.getExperienceText() != null ? overrideBasic.getExperienceText() : baseBasic.getExperienceText()
                )
                        : baseBasic
        );

        return result;
    }




}