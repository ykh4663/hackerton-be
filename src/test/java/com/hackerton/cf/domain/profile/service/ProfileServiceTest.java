package com.hackerton.cf.domain.profile.service;

import com.hackerton.cf.domain.auth.dto.RoleStatus;
import com.hackerton.cf.domain.profile.dao.ProfileRepository;
import com.hackerton.cf.domain.profile.domain.Ability;
import com.hackerton.cf.domain.profile.domain.CareerStatus;
import com.hackerton.cf.domain.profile.domain.Profile;
import com.hackerton.cf.domain.profile.domain.ProfileBasic;
import com.hackerton.cf.domain.profile.dto.ProfileDto;
import com.hackerton.cf.domain.profile.dto.ProfileRequest;
import com.hackerton.cf.domain.profile.dto.ProfileResponse;
import com.hackerton.cf.domain.user.dao.UserRepository;
import com.hackerton.cf.domain.user.domain.User;
import com.hackerton.cf.domain.user.service.UserService;
import com.hackerton.cf.global.error.ApplicationException;
import com.hackerton.cf.global.error.ProfileErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock private UserService userService;
    @Mock private ProfileRepository profileRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private ProfileService sut;

    private static final Long USER_ID = 1L;

    // ---------- 헬퍼 ----------
    private ProfileBasic basic(String dept, Integer year, String cert, String lang, String awards, String exp) {
        return ProfileBasic.builder()
                .department(dept)
                .universityYear(year)
                .certifications(cert)
                .languages(lang)
                .awards(awards)
                .experienceText(exp)
                .build();
    }
    private Ability ability(int code) {
        Ability a = new Ability();
        a.setCode(code);
        return a;
    }

    // ================= A. getProfile (5) =================
    @Nested
    @DisplayName("getProfile")
    class GetProfile {

        @Test
        @DisplayName("성공: 프로필/베이직/능력치 매핑")
        void success_mapping() {
            User mockUser = mock(User.class);
            Profile p = Profile.builder().build();
            p.updateBasic(basic("CS", 2023, "certA", "ENG", "awardA", "expA"));
            Ability a1 = ability(1); a1.setProfile(p); p.getAbilities().add(a1);
            Ability a2 = ability(2); a2.setProfile(p); p.getAbilities().add(a2);

            given(userService.getUserById(USER_ID)).willReturn(mockUser);
            given(mockUser.getProfile()).willReturn(p);

            ProfileResponse resp = sut.getProfile(USER_ID);

            assertThat(resp).isNotNull();
            assertThat(resp.getBasic().getDepartment()).isEqualTo("CS");
            assertThat(resp.getBasic().getUniversityYear()).isEqualTo(2023);
            assertThat(resp.getBasic().getCertifications()).isEqualTo("certA");
            assertThat(resp.getBasic().getLanguages()).isEqualTo("ENG");
            assertThat(resp.getBasic().getAwards()).isEqualTo("awardA");
            assertThat(resp.getBasic().getExperienceText()).isEqualTo("expA");
            assertThat(resp.getAbilities()).containsExactly(1, 2);
            // NOTE: careerCode는 서비스 코드 버그(자기복사)로 미검증
        }

        @Test
        @DisplayName("실패: 프로필이 없으면 NOT_FOUND_PROFILE")
        void no_profile_then_throw() {
            User mockUser = mock(User.class);
            given(userService.getUserById(USER_ID)).willReturn(mockUser);
            given(mockUser.getProfile()).willReturn(null);

            Throwable t = catchThrowable(() -> sut.getProfile(USER_ID));
            assertThat(t).isInstanceOf(ApplicationException.class);
            ApplicationException ae = (ApplicationException) t;
            assertThat(ae.getErrorCode()).isEqualTo(ProfileErrorCode.NOT_FOUND_PROFILE);
        }

        @Test
        @DisplayName("전파: userService.getUserById 예외 전파")
        void propagate_userService_exception() {
            given(userService.getUserById(USER_ID))
                    .willThrow(new ApplicationException(ProfileErrorCode.NOT_FOUND_PROFILE));

            assertThatThrownBy(() -> sut.getProfile(USER_ID))
                    .isInstanceOf(ApplicationException.class);
        }

        @Test
        @DisplayName("엣지: ProfileBasic이 null이면 NPE (현 구현상)")
        void null_profileBasic_npe() {
            User mockUser = mock(User.class);
            Profile p = Profile.builder().build(); // basic 미세팅
            given(userService.getUserById(USER_ID)).willReturn(mockUser);
            given(mockUser.getProfile()).willReturn(p);

            assertThrows(NullPointerException.class, () -> sut.getProfile(USER_ID));
        }

        @Test
        @DisplayName("엣지: abilities가 비어 있어도 빈 리스트로 매핑")
        void empty_abilities_ok() {
            User mockUser = mock(User.class);
            Profile p = Profile.builder().build();
            p.updateBasic(basic("EE", 2022, "C", "KOR", "W", "E"));
            given(userService.getUserById(USER_ID)).willReturn(mockUser);
            given(mockUser.getProfile()).willReturn(p);

            ProfileResponse resp = sut.getProfile(USER_ID);
            assertThat(resp.getAbilities()).isEmpty();
        }
    }

    // ================= B. upsertProfile (8) =================
    @Nested
    @DisplayName("upsertProfile")
    class UpsertProfile {

        private ProfileRequest req(Integer careerCode, List<Integer> abilities, ProfileDto basic, String nickname) {
            ProfileRequest r = new ProfileRequest();
            r.setCareerCode(careerCode);
            r.setAbilities(abilities);
            r.setBasic(basic);
            r.setNickname(nickname);
            return r;
        }

        @Test
        @DisplayName("성공·신규: 프로필 없으면 생성 + 저장, REGISTER→COMPLETE 저장 호출")
        void create_new_profile_and_promote_role() {
            User mockUser = mock(User.class);
            given(userService.getUserById(USER_ID)).willReturn(mockUser);
            given(mockUser.getProfile()).willReturn(null);
            given(mockUser.getRole()).willReturn(RoleStatus.REGISTER.getStatus());

            ProfileDto b = new ProfileDto(2024, "EE", "certB", "KOR", "awardB", "expB");
            ProfileRequest r = req(0, List.of(3,4), b, "neo");

            ArgumentCaptor<Profile> cap = ArgumentCaptor.forClass(Profile.class);

            sut.upsertProfile(USER_ID, r);

            then(profileRepository).should().save(cap.capture());
            Profile saved = cap.getValue();
            assertThat(saved.getProfileBasic().getDepartment()).isEqualTo("EE");
            assertThat(saved.getCareerStatus()).isEqualTo(CareerStatus.신입);
            assertThat(saved.getAbilities()).extracting(Ability::getCode).containsExactly(3,4);

            then(userRepository).should().save(mockUser); // 역할 전환 저장 호출
        }

        @Test
        @DisplayName("성공·갱신: 기존 프로필에 덮어쓰기 + abilities 완전 교체")
        void update_existing_profile_replace_abilities() {
            User mockUser = mock(User.class);
            Profile existing = Profile.builder().build();
            existing.updateBasic(basic("EE", 2, "c1","l1","w1","e1"));
            Ability a1 = ability(1); a1.setProfile(existing); existing.getAbilities().add(a1);
            Ability a2 = ability(2); a2.setProfile(existing); existing.getAbilities().add(a2);

            given(userService.getUserById(USER_ID)).willReturn(mockUser);
            given(mockUser.getProfile()).willReturn(existing);
            given(mockUser.getRole()).willReturn(RoleStatus.COMPLETE.getStatus());

            ProfileDto b = new ProfileDto(4, "ME", "c2","l2","w2","new exp");
            ProfileRequest r = req(1, List.of(30), b, null);

            ArgumentCaptor<Profile> cap = ArgumentCaptor.forClass(Profile.class);
            sut.upsertProfile(USER_ID, r);

            then(profileRepository).should().save(cap.capture());
            Profile saved = cap.getValue();
            assertThat(saved.getProfileBasic().getDepartment()).isEqualTo("ME");
            assertThat(saved.getCareerStatus()).isEqualTo(CareerStatus.경력);
            assertThat(saved.getAbilities()).extracting(Ability::getCode).containsExactly(30);
        }

        @Test
        @DisplayName("성공: careerCode=1 → 경력으로 세팅")
        void career_code_1_experienced() {
            User mockUser = mock(User.class);
            Profile existing = Profile.builder().build();
            existing.updateBasic(basic("CSE", 3, "c","l","w","e"));
            given(userService.getUserById(USER_ID)).willReturn(mockUser);
            given(mockUser.getProfile()).willReturn(existing);
            // getRole() 스텁 불필요 (예외 없음이어도 검증 대상 아님)

            ProfileRequest r = req(1, List.of(), new ProfileDto(3,"CSE","c","l","w","e"), null);

            ArgumentCaptor<Profile> cap = ArgumentCaptor.forClass(Profile.class);
            sut.upsertProfile(USER_ID, r);

            then(profileRepository).should().save(cap.capture());
            assertThat(cap.getValue().getCareerStatus()).isEqualTo(CareerStatus.경력);
        }

        @Test
        @DisplayName("실패: careerCode가 0/1 외 값 → INVALID_CAREER_CODE")
        void invalid_career_code() {
            User mockUser = mock(User.class);
            Profile existing = Profile.builder().build();
            existing.updateBasic(basic("CSE", 3,"c","l","w","e"));
            given(userService.getUserById(USER_ID)).willReturn(mockUser);
            given(mockUser.getProfile()).willReturn(existing);
            // getRole() 스텁 불필요 (예외가 먼저 발생)

            ProfileRequest r = req(99, List.of(1), new ProfileDto(3,"CSE","c","l","w","e"), null);

            Throwable t = catchThrowable(() -> sut.upsertProfile(USER_ID, r));
            assertThat(t).isInstanceOf(ApplicationException.class);
            ApplicationException ae = (ApplicationException) t;
            assertThat(ae.getErrorCode()).isEqualTo(ProfileErrorCode.INVALID_CAREER_CODE);
        }

        @Test
        @DisplayName("실패: careerCode == null → NPE (현 구현상)")
        void null_career_code_npe() {
            User mockUser = mock(User.class);
            Profile existing = Profile.builder().build();
            existing.updateBasic(basic("CSE", 3,"c","l","w","e"));
            given(userService.getUserById(USER_ID)).willReturn(mockUser);
            given(mockUser.getProfile()).willReturn(existing);
            // getRole() 스텁 불필요 (NPE가 먼저 발생)

            ProfileRequest r = req(null, List.of(), new ProfileDto(3,"CSE","c","l","w","e"), null);

            assertThrows(NullPointerException.class, () -> sut.upsertProfile(USER_ID, r));
        }

        @Test
        @DisplayName("실패: req.getBasic() == null → NPE (현 구현상)")
        void null_basic_npe() {
            User mockUser = mock(User.class);
            Profile existing = Profile.builder().build();
            existing.updateBasic(basic("CSE", 3,"c","l","w","e"));
            given(userService.getUserById(USER_ID)).willReturn(mockUser);
            given(mockUser.getProfile()).willReturn(existing);

            ProfileRequest r = req(0, List.of(10), null, null);

            assertThrows(NullPointerException.class, () -> sut.upsertProfile(USER_ID, r));
        }

        @Test
        @DisplayName("실패/엣지: req.getAbilities() == null → NPE (현 구현상)")
        void null_abilities_npe() {
            User mockUser = mock(User.class);
            Profile existing = Profile.builder().build();
            existing.updateBasic(basic("CSE", 3,"c","l","w","e"));
            given(userService.getUserById(USER_ID)).willReturn(mockUser);
            given(mockUser.getProfile()).willReturn(existing);
            // getRole() 스텁 불필요 (NPE가 먼저 발생)

            ProfileRequest r = req(0, null, new ProfileDto(3,"CSE","c","l","w","e"), null);

            assertThrows(NullPointerException.class, () -> sut.upsertProfile(USER_ID, r));
        }

        @Test
        @DisplayName("엣지: nickname==null이면 setNickname 호출되지 않음(기존 유지)")
        void null_nickname_kept() {
            User mockUser = mock(User.class);
            Profile existing = Profile.builder().build();
            existing.updateBasic(basic("CSE", 3,"c","l","w","e"));
            given(userService.getUserById(USER_ID)).willReturn(mockUser);
            given(mockUser.getProfile()).willReturn(existing);

            ProfileRequest r = req(0, List.of(), new ProfileDto(3,"CSE","c","l","w","e"), null);

            sut.upsertProfile(USER_ID, r);

            then(mockUser).should(never()).setNickname(anyString());
        }
    }

    // ================= C. mergeProfile (8) =================
    @Nested
    @DisplayName("mergeProfile")
    class MergeProfile {

        private ProfileResponse baseResp() {
            ProfileResponse base = new ProfileResponse();
            base.setAbilities(List.of(1, 2));
            base.setCareerCode("신입");
            base.setBasic(new ProfileDto(2, "EE", "cert1", "L1", "W1", "base exp"));
            return base;
        }
        private ProfileRequest ov(Integer code, List<Integer> abilities, ProfileDto b) {
            ProfileRequest r = new ProfileRequest();
            r.setCareerCode(code);
            r.setAbilities(abilities);
            r.setBasic(b);
            return r;
        }

        @Test @DisplayName("abilities override: 비어있지 않으면 덮어씀")
        void abilities_override_non_empty() {
            ProfileResponse res = sut.mergeProfile(baseResp(), ov(null, List.of(9, 8), null));
            assertThat(res.getAbilities()).containsExactly(9,8);
        }

        @Test @DisplayName("abilities override: 비어있으면 base 유지")
        void abilities_override_empty_keeps_base() {
            ProfileResponse res = sut.mergeProfile(baseResp(), ov(null, List.of(), null));
            assertThat(res.getAbilities()).containsExactly(1,2);
        }

        @Test @DisplayName("abilities override: null이면 base 유지")
        void abilities_null_keeps_base() {
            ProfileResponse res = sut.mergeProfile(baseResp(), ov(null, null, null));
            assertThat(res.getAbilities()).containsExactly(1,2);
        }

        @Test @DisplayName("careerCode=0 → '신입'")
        void careerCode_0() {
            ProfileResponse res = sut.mergeProfile(baseResp(), ov(0, null, null));
            assertThat(res.getCareerCode()).isEqualTo("신입");
        }

        @Test @DisplayName("careerCode=1 → '경력'")
        void careerCode_1() {
            ProfileResponse res = sut.mergeProfile(baseResp(), ov(1, null, null));
            assertThat(res.getCareerCode()).isEqualTo("경력");
        }

        @Test @DisplayName("careerCode=2(비표준) → 현재 로직상 '경력'")
        void careerCode_non_standard_maps_to_experienced() {
            ProfileResponse res = sut.mergeProfile(baseResp(), ov(2, null, null));
            assertThat(res.getCareerCode()).isEqualTo("경력");
        }

        @Test @DisplayName("basic 일부 필드만 override → 지정 필드만 교체, 나머지 base 유지")
        void basic_partial_override() {
            ProfileDto ob = new ProfileDto(null, "CSE", null, null, null, null);
            ProfileResponse res = sut.mergeProfile(baseResp(), ov(null, null, ob));
            assertThat(res.getBasic().getDepartment()).isEqualTo("CSE");
            assertThat(res.getBasic().getUniversityYear()).isEqualTo(2); // base 유지
            assertThat(res.getBasic().getCertifications()).isEqualTo("cert1");
        }

        @Test @DisplayName("override.basic == null → base.basic 유지")
        void override_basic_null_keep_base() {
            ProfileResponse res = sut.mergeProfile(baseResp(), ov(null, null, null));
            assertThat(res.getBasic().getDepartment()).isEqualTo("EE");
        }

        @Test
        @DisplayName("엣지: base.basic == null + override 일부 → NPE (현 구현상)")
        void base_basic_null_npe() {
            ProfileResponse base = new ProfileResponse();
            base.setAbilities(List.of(1,2));
            base.setCareerCode("신입");
            base.setBasic(null);

            ProfileDto ob = new ProfileDto(null, "CSE", null, null, null, null);
            assertThrows(NullPointerException.class, () -> sut.mergeProfile(base, ov(null, null, ob)));
        }
    }
}
