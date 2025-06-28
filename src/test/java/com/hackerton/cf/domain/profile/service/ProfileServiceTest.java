package com.hackerton.cf.domain.profile.service;

import com.hackerton.cf.domain.auth.dto.RoleStatus;
import com.hackerton.cf.domain.profile.dao.ProfileRepository;
import com.hackerton.cf.domain.profile.domain.Ability;
import com.hackerton.cf.domain.profile.domain.CareerStatus;
import com.hackerton.cf.domain.profile.domain.Profile;
import com.hackerton.cf.domain.profile.domain.ProfileBasic;
import com.hackerton.cf.domain.profile.dto.ProfileBasicDto;
import com.hackerton.cf.domain.profile.dto.ProfileRequest;
import com.hackerton.cf.domain.profile.dto.ProfileResponse;
import com.hackerton.cf.domain.user.dao.UserRepository;
import com.hackerton.cf.domain.user.domain.User;
import com.hackerton.cf.domain.user.service.UserService;
import com.hackerton.cf.global.error.ApplicationException;
import com.hackerton.cf.global.error.ProfileErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock private UserService userService;
    @Mock private ProfileRepository profileRepository;
    @Mock private UserRepository userRepository;          // ← 추가
    @InjectMocks private ProfileService profileService;

    private final Long USER_ID = 1L;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);
    }

    @Test
    void getProfile_whenNoProfile_throwsApplicationException() {
        when(userService.getUserById(USER_ID)).thenReturn(mockUser);
        when(mockUser.getProfile()).thenReturn(null);

        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> profileService.getProfile(USER_ID)
        );
        assertEquals(ProfileErrorCode.NOT_FOUND_PROFILE, ex.getErrorCode());
    }

    @Test
    void getProfile_whenProfileExists_returnsBasicAndAbilities() {
        // 1) Profile 엔티티와 Basic 세팅
        Profile existing = new Profile();
        ProfileBasic basic = ProfileBasic.builder()
                .universityYear(2023)
                .department("CS")
                .certifications("certA")       // ProfileBasic은 문자열 필드
                .languages("ENG")
                .awards("awardA")
                .experienceText("expA")
                .build();
        existing.updateBasic(basic);

        // 2) Ability 리스트 직접 추가
        Ability a1 = new Ability(); a1.setCode(1); a1.setProfile(existing);
        Ability a2 = new Ability(); a2.setCode(2); a2.setProfile(existing);
        existing.getAbilities().add(a1);
        existing.getAbilities().add(a2);

        // 3) 스텁 설정
        when(userService.getUserById(USER_ID)).thenReturn(mockUser);
        when(mockUser.getProfile()).thenReturn(existing);

        // 4) 실행
        ProfileResponse resp = profileService.getProfile(USER_ID);

        // 5) 검증
        assertNotNull(resp);
        assertEquals(2023, resp.getBasic().getUniversityYear());
        assertEquals("CS", resp.getBasic().getDepartment());
        assertEquals("certA", resp.getBasic().getCertifications());
        assertEquals("expA", resp.getBasic().getExperienceText());
        assertEquals(List.of(1, 2), resp.getAbilities());
    }

    @Test
    void upsertProfile_invalidCareerCode_throwsApplicationException() {
        when(userService.getUserById(USER_ID)).thenReturn(mockUser);
        when(mockUser.getProfile()).thenReturn(null);

        ProfileRequest req = new ProfileRequest();
        // 빈 기본 정보 객체를 넣어서 NPE 방지
        req.setBasic(new ProfileBasicDto(null, null, null, null, null, null));
        req.setAbilities(List.of());         // 빈 리스트
        req.setCareerCode(99);               // 잘못된 코드

        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> profileService.upsertProfile(USER_ID, req)
        );
        assertEquals(ProfileErrorCode.INVALID_CAREER_CODE, ex.getErrorCode());
    }

    @Test
    void upsertProfile_whenNewProfile_createsAndSavesProfile_andUpdatesUserRole() {
        when(userService.getUserById(USER_ID)).thenReturn(mockUser);
        when(mockUser.getProfile()).thenReturn(null);
        when(mockUser.getRole()).thenReturn(RoleStatus.REGISTER.getStatus());

        ProfileBasicDto basicDto = new ProfileBasicDto(
                2024, "EE", "certB", "KOR", "awardB", "expB"
        );
        ProfileRequest req = new ProfileRequest();
        req.setBasic(basicDto);
        req.setAbilities(List.of(3, 4));
        req.setCareerCode(0);

        profileService.upsertProfile(USER_ID, req);

        // ProfileRepository.save 호출 확인
        verify(profileRepository).save(any(Profile.class));
        // UserRepository.save 호출 확인
        verify(userRepository).save(mockUser);
    }

    @Test
    void mergeProfile_overrideValues_prefersOverride() {
        ProfileBasicDto baseBasic = new ProfileBasicDto(2024, "CS",
                "cert1", "ENG", "award1", "exp1");
        ProfileResponse base = new ProfileResponse();
        base.setAbilities(List.of(1, 2));
        base.setCareerCode("신입");
        base.setBasic(baseBasic);

        ProfileRequest override = new ProfileRequest();
        override.setAbilities(List.of(3, 4));
        override.setCareerCode(1);
        override.setBasic(new ProfileBasicDto(
                2025, "EE", "cert2", "KOR", "award2", "exp2"
        ));

        ProfileResponse result = profileService.mergeProfile(base, override);

        assertEquals(List.of(3, 4), result.getAbilities());
        assertEquals("경력", result.getCareerCode());
        assertEquals(2025, result.getBasic().getUniversityYear());
        assertEquals("EE", result.getBasic().getDepartment());
    }

    @Test
    void mergeProfile_partialOverride_fallsBackToBase() {
        ProfileBasicDto baseBasic = new ProfileBasicDto(2024, "CS",
                "cert1", "ENG", "award1", "exp1");
        ProfileResponse base = new ProfileResponse();
        base.setAbilities(List.of(1, 2));
        base.setCareerCode("신입");
        base.setBasic(baseBasic);

        ProfileRequest override = new ProfileRequest();
        override.setAbilities(null);
        override.setCareerCode(null);
        override.setBasic(new ProfileBasicDto(
                null, "ME", null, null, null, null
        ));

        ProfileResponse result = profileService.mergeProfile(base, override);

        assertEquals(List.of(1, 2), result.getAbilities());
        assertEquals("신입", result.getCareerCode());
        assertEquals("ME", result.getBasic().getDepartment());
        assertEquals(2024, result.getBasic().getUniversityYear());
    }
}